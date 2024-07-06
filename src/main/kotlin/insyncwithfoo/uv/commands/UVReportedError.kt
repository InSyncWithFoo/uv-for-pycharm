package insyncwithfoo.uv.commands

import com.intellij.execution.process.ProcessOutput


class UVReportedError(
    private val exitCode: Int? = null,
    private val stdout: String? = null,
    private val stderr: String? = null
) : Exception(MESSAGE) {
    
    constructor(processOutput: ProcessOutput) :
        this(processOutput.exitCode, processOutput.stdout, processOutput.stderr)
    
    companion object {
        private const val MESSAGE = "UV reported an error"
    }
    
}
