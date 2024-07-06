package insyncwithfoo.uv.commands

import com.intellij.execution.process.ProcessOutput
import java.nio.file.Path


internal class CreateVenvCommand(
    override val executable: Path,
    override val workingDirectory: Path,
    private val baseInterpreter: Path,
    private val name: String? = null
) : Command<ProcessOutput>() {
    
    override val arguments: List<String>
        get() = listOfNotNull(
            "venv", name,
            "--python", baseInterpreter.toString()
        )
    
    override fun run(): ProcessOutput {
        return runProcess()
    }
    
}
