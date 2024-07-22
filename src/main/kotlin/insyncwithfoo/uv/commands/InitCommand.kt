package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class InitCommand(
    override val executable: Path,
    override val workingDirectory: Path
) : Command<Successful>() {
    
    override val arguments: List<String>
        get() = listOf("init")
    
    override val runningMessage: String
        get() = "Initializing..."
    
    override fun run(): Successful {
        return runProcess().checkSuccess(LOGGER)
    }
    
}
