package insyncwithfoo.uv.commands

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import java.nio.file.Path


private val GeneralCommandLine.handler: CapturingProcessHandler
    get() = CapturingProcessHandler(this)


internal abstract class Command<Output> {
    
    abstract val executable: Path
    abstract val arguments: List<String>
    
    open val workingDirectory: Path? = null
    open var timeout: Int = -1
    
    open val runningMessage: String = "Running command..."
    
    private val commandLine: GeneralCommandLine
        get() = GeneralCommandLine(executable.toString()).apply {
            withWorkDirectory(this@Command.workingDirectory?.toString())
            withCharset(Charsets.UTF_8)
            addParameters(arguments)
        }
    
    abstract fun run(): Output
    
    fun asCopyableString() = commandLine.commandLineString
    
    protected fun runProcess(): ProcessOutput =
        commandLine.handler.runProcess(timeout)
    
    companion object {
        @JvmStatic
        protected val LOGGER = Logger.getInstance(Command::class.java)
    }
    
}
