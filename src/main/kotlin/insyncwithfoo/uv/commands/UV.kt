package insyncwithfoo.uv.commands

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.uv.configurations.changeUVConfigurations
import insyncwithfoo.uv.configurations.uvConfigurations
import insyncwithfoo.uv.path
import insyncwithfoo.uv.toPathOrNull
import java.nio.file.Path


/**
 * Handles interactions with the executable.
 */
internal sealed class UV {
    
    protected abstract val executable: Path
    protected abstract val workingDirectory: Path?
    
    protected var blocking = false
    
    // private fun build(command: Command): Command {
    //     // TODO: Make the timeout limit configurable
    //     command.workingDirectory = workingDirectory
    //     command.timeout = 10_000
    //    
    //     return command
    // }
    //
    // protected fun run(command: Command, runner: Command.() -> ProcessOutput): ProcessOutput {
    //     return build(command).runner()
    // }
    
    companion object {
        
        @JvmStatic
        protected val LOGGER = Logger.getInstance(UV::class.java)
        
        var executable: Path?
            get() = savedExecutable ?: detectExecutable()
            set(value) {
                changeUVConfigurations { executable = value.toString() }
            }
        
        private val savedExecutable: Path?
            get() = uvConfigurations.executable?.toPathOrNull()
        
        fun detectExecutable(): Path? {
            val fileName = when {
                SystemInfo.isWindows -> "uv.exe"
                else -> "uv"
            }
            
            return PathEnvironmentVariableUtil.findInPath(fileName)?.toPath()
        }
        
        private fun create(executable: Path): FreeUV {
            return FreeUV(executable)
        }
        
        fun create(executable: Path, projectPath: Path): LockedUV {
            return LockedUV(executable, projectPath)
        }
        
        fun create(executable: Path, project: Project): ProjectUV {
            return ProjectUV(executable, project)
        }
        
        fun createFree(): FreeUV? {
            return executable?.let { create(it) }
        }
        
        fun createLocked(projectPath: Path): LockedUV? {
            return executable?.let { create(it, projectPath) }
        }
        
        fun createForProject(project: Project): ProjectUV? {
            return executable?.let { create(it, project) }
        }
        
    }
    
}


/**
 * Handles commands that are not dependent on a context.
 */
internal open class FreeUV(override val executable: Path) : UV() {
    
    override val workingDirectory: Path? = null
    
    fun version(): String {
        return VersionCommand(executable).run()
    }
    
}


/**
 * Handles commands that are bound to a path but not a project.
 */
internal class LockedUV(executable: Path, override val workingDirectory: Path) : FreeUV(executable) {
    
    fun createVenv(baseInterpreter: Path, name: String? = null): Successful {
        val output = VenvCommand(executable, workingDirectory, baseInterpreter, name).run()
        return output.checkSuccess(LOGGER)
    }
    
}


/**
 * Handles commands that are bound to a project.
 */
internal class ProjectUV(executable: Path, private val project: Project) : FreeUV(executable) {
    
    override val workingDirectory: Path
        get() = project.path!!
    
    fun add(target: PythonPackageSpecification): Successful {
        return AddCommand(executable, workingDirectory, target).run()
    }
    
    fun remove(target: String): Successful {
        return RemoveCommand(executable, workingDirectory, target).run()
    }
    
    fun list(): InstalledPackages? {
        return PipListCommand(executable, workingDirectory).run()
    }
    
    fun update(specification: PythonPackageSpecification): Successful {
        return UpgradeCommand(executable, workingDirectory, specification).run()
    }
    
    fun sync(): Successful {
        return SyncCommand(executable, workingDirectory).run()
    }
    
    fun init(): Successful {
        return InitCommand(executable, workingDirectory).run()
    }
    
}
