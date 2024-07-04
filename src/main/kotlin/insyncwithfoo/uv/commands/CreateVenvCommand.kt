package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class CreateVenvCommand(
    override val executable: Path,
    override val workingDirectory: Path,
    private val baseInterpreter: Path,
    private val name: String? = null
) : Command() {
    
    override val arguments: List<String>
        get() = listOfNotNull(
            "venv", name,
            "--python", baseInterpreter.toString()
        )
    
}
