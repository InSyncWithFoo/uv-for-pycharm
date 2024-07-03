package insyncwithfoo.uv.generator

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.Task.WithResult
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.StandardFileSystems
import com.jetbrains.python.sdk.PySdkSettings
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.associateWithModule
import com.jetbrains.python.sdk.excludeInnerVirtualEnv
import com.jetbrains.rd.util.ExecutionException
import insyncwithfoo.uv.UVSdkAdditionalData
import insyncwithfoo.uv.commands.UV
import insyncwithfoo.uv.message
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.toPathOrNull
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import kotlin.io.path.div


internal class VenvCreator(
    private val executable: Path,
    private val projectPath: Path,
    private val baseSdk: Sdk,
    private val directoryName: String = ".venv"
) {
    
    private val suggestedName: String
        get() = "${baseSdk.name} [uv] ($directoryName)"
    
    private val venvRoot: Path
        get() = projectPath / directoryName
    
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
        UV.savedExecutablePath = executable
        
        return newSdk
    }
    
    /**
     * @see com.jetbrains.python.sdk.createSdkByGenerateTask
     */
    private fun createAndSetUpSdk(): Sdk? {
        val interpreterPath = createVenvAndReturnInterpreterPath()
        val interpreterVirtualFile = StandardFileSystems.local().refreshAndFindFileByPath(interpreterPath)!!
        
        return SdkConfigurationUtil.setupSdk(
            emptyList<Sdk>().toTypedArray(),
            interpreterVirtualFile,
            PythonSdkType.getInstance(),
            false,
            UVSdkAdditionalData(),
            suggestedName
        )
        
    }
    
    /**
     * @see com.jetbrains.python.packaging.PyTargetEnvironmentPackageManager.createVirtualEnv
     */
    private fun createVenvAndReturnInterpreterPath(): String {
        // TODO: Use runWithModalProgressBlocking instead.
        @Suppress("DialogTitleCapitalization", "UsagesOfObsoleteApi")
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            { createVenv() },
            message("newProjectPanel.progress.creatingVenv"),
            false,
            null
        )
        
        val interpreterPath = PythonSdkUtil.getPythonExecutable(venvRoot.toString())
        
        if (interpreterPath == null) {
            somethingIsWrong("Cannot create virtual environment.")
            error("Cannot create virtual environment at $venvRoot using $executable. See logs for details.")
        }
        
        return interpreterPath
    }
    
    // FIXME:
    // This seems to require write access,
    // but if it is wrapped in runWriteAction/writeAction,
    // it will never run.
    private fun createVenv() {
        val baseInterpreterPath = baseSdk.homePath!!.toPathOrNull()!!
        UV.create(executable, projectPath).createVenv(baseInterpreterPath, directoryName)
    }
    
}
