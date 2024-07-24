package insyncwithfoo.uv.generator

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.sdk.PySdkSettings
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.associateWithModule
import com.jetbrains.python.sdk.excludeInnerVirtualEnv
import insyncwithfoo.uv.UVSdkAdditionalData
import insyncwithfoo.uv.commands.UV
import insyncwithfoo.uv.message
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.toPathOrNull
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.name


internal class VenvCreator(
    private val executable: Path,
    private val projectPath: Path,
    private val baseSdk: Sdk
) {
    
    /**
     * Example: "Python 3.12 &#91;uv&#93; (my-project)"
     */
    private val suggestedName: String
        get() {
            val pythonAndVersion = PythonSdkType.suggestBaseSdkName(baseSdk.homePath!!)
            return "$pythonAndVersion [uv] (${projectPath.name})"
        }
    
    /**
     * Expected to be set by [createVenv]
     * once the virtual environment is created.
     */
    private lateinit var venvDirectoryName: String
    
    private val venvRoot: Path
        get() = projectPath / venvDirectoryName
    
    /**
     * @see com.jetbrains.python.sdk.configuration.createVirtualEnvSynchronously
     */
    fun createSdk(): Sdk? {
        val newSdk = createAndSetUpSdk()
        val venvRoot = venvRoot.toString().replace("\\", "/")
        val project: Project? = null
        
        newSdk?.associateWithModule(module = null, projectPath.toString()) ?: return null
        project.excludeInnerVirtualEnv(newSdk)
        PySdkSettings.instance.onVirtualEnvCreated(baseSdk, venvRoot, projectPath.toString())
        
        runWriteAction { newSdk.sdkModificator.commitChanges() }
        UV.executable = executable
        
        return newSdk
    }
    
    /**
     * @see com.jetbrains.python.sdk.createSdkByGenerateTask
     */
    private fun createAndSetUpSdk(): Sdk? {
        createVenvSynchronously()
        
        return SdkConfigurationUtil.setupSdk(
            emptyList<Sdk>().toTypedArray(),
            findNewlyCreatedInterpreter(),
            PythonSdkType.getInstance(),
            false,
            UVSdkAdditionalData(),
            suggestedName
        )
    }
    
    /**
     * @see com.jetbrains.python.packaging.PyTargetEnvironmentPackageManager.createVirtualEnv
     */
    private fun findNewlyCreatedInterpreter(): VirtualFile {
        val interpreterPath = PythonSdkUtil.getPythonExecutable(venvRoot.toString())
        
        if (interpreterPath == null) {
            somethingIsWrong(message("messages.cannotCreateVenv.body"))
            error("Cannot create virtual environment at $venvRoot using $executable.")
        }
        
        return StandardFileSystems.local().refreshAndFindFileByPath(interpreterPath)!!
    }
    
    /**
     * @see com.jetbrains.python.packaging.PyTargetEnvironmentPackageManager.createVirtualEnv
     */
    @Suppress("DialogTitleCapitalization")
    private fun createVenvSynchronously() {
        val (canBeCanceled, project) = Pair(false, null)
        
        // TODO: Use runWithModalProgressBlocking instead.
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            { createVenv() },
            message("progresses.title.venv"),
            canBeCanceled, project
        )
    }
    
    private fun createVenv() {
        val baseInterpreterPath = baseSdk.homePath!!.toPathOrNull()!!
        val uv = UV.create(executable, projectPath)
        
        when (val newVenvName = uv.createVenv(baseInterpreterPath)) {
            null -> somethingIsWrong(message("messages.uvReportedError.body"))
            else -> venvDirectoryName = newVenvName
        }
    }
    
}
