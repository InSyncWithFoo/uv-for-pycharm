package insyncwithfoo.uv.commands

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.execution.process.ProcessOutput
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.uv.path
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.toPathOrNull
import java.nio.file.Path


private const val UV_EXECUTABLE_PATH = "insyncwithfoo.uv.executablePath"


private var PropertiesComponent.uvExecutablePath: Path?
    get() = getValue(UV_EXECUTABLE_PATH)?.toPathOrNull()
    set(value) {
        setValue(UV_EXECUTABLE_PATH, value.toString())
    }


internal sealed class UV {
    
    protected abstract val executable: Path
    protected abstract val workingDirectory: Path?
    
    protected var blocking = false
    
    private fun build(command: Command): Command {
        // TODO: Make the timeout limit configurable
        command.workingDirectory = workingDirectory
        command.timeout = 10_000
        
        return command
    }
    
    protected fun run(command: Command, runner: Command.() -> ProcessOutput): ProcessOutput {
        return build(command).runner()
    }
    
    companion object {
        
        @JvmStatic
        protected val LOGGER = Logger.getInstance(UV::class.java)
        
        var savedExecutablePath by PropertiesComponent.getInstance()::uvExecutablePath
        
        fun detectExecutable(): Path? {
            val fileName = when {
                SystemInfo.isWindows -> "uv.exe"
                else -> "uv"
            }
            
            return PathEnvironmentVariableUtil.findInPath(fileName)?.toPath()
        }
        
        fun savedOrDetectExecutablePath() =
            savedExecutablePath ?: detectExecutable()
        
        fun create(executable: Path): FreeUV {
            return FreeUV(executable)
        }
        
        fun create(executable: Path, projectPath: Path): LockedUV {
            return LockedUV(executable, projectPath)
        }
        
        fun create(executable: Path, project: Project): ProjectUV {
            return ProjectUV(executable, project)
        }
        
    }
    
}


internal open class FreeUV(override val executable: Path) : UV() {
    
    override val workingDirectory: Path? = null
    
    fun version(): String? {
        val output = VersionCommand(executable).run()
        
        return when {
            output.checkSuccess(LOGGER) -> output.stdout
            else -> null
        }
    }
    
}


internal class LockedUV(executable: Path, override val workingDirectory: Path) : FreeUV(executable) {
    
    fun createVenv(baseInterpreter: Path, name: String? = null) {
        val command = CreateVenvCommand(executable, baseInterpreter, name)
        val output = command.run()
        
        if (!output.checkSuccess(LOGGER)) {
            somethingIsWrong("uv reported an error. See the logs for details.")
        }
    }
    
}


/**
 * Handles interactions with the executable.
 */
internal class ProjectUV(executable: Path, private val project: Project) : FreeUV(executable) {
    
    override val workingDirectory: Path?
        get() = project.path
    
    fun add(target: PythonPackageSpecification) {
        // run(AddCommand(executable, target))
        TODO("Not yet implemented")
    }
    
    fun remove(target: String) {
        // run(RemoveCommand(executable, target))
        TODO("Not yet implemented")
    }
    
    fun list() {
        TODO("Not yet implemented")
    }
    
    fun show(target: String) {
        TODO("Not yet implemented")
    }
    
}
