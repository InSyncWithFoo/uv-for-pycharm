package insyncwithfoo.uv.commands

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path


private val GeneralCommandLine.handler: CapturingProcessHandler
    get() = CapturingProcessHandler(this)


@Suppress("unused")
@Serializable
private class ProcessOutputSurrogate (
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val isTimeout: Boolean,
    val isCancelled: Boolean
) {
    override fun toString() = Json.encodeToString(this)
}


private fun ProcessOutputSurrogate(processOutput: ProcessOutput) = with(processOutput) {
    ProcessOutputSurrogate(stdout, stderr, exitCode, isTimeout, isCancelled)
}


internal abstract class Command<Output> {
    
    abstract val executable: Path
    abstract val arguments: List<String>
    
    open val workingDirectory: Path? = null
    open var timeout: Int = -1
    
    open val runningMessage: String = "Running command..."
    
    @Suppress("UsagesOfObsoleteApi")
    private val commandLine: GeneralCommandLine
        get() = GeneralCommandLine(executable.toString()).apply {
            withWorkDirectory(this@Command.workingDirectory?.toString())
            withCharset(Charsets.UTF_8)
            addParameters(arguments)
        }
    
    abstract fun run(): Output
    
    override fun toString() = commandLine.commandLineString
    
    protected fun runAndLogProcess(): ProcessOutput {
        LOGGER.info("Running: $this")
        return runProcess().also { LOGGER.info("Output: ${ProcessOutputSurrogate(it)}") }
    }
    
    private fun runProcess() = commandLine.handler.runProcess(timeout)
    
    companion object {
        @JvmStatic
        protected val LOGGER = Logger.getInstance(Command::class.java)
    }
    
}
