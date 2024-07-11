package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class SyncCommand(
    override val executable: Path,
    override val workingDirectory: Path
) : Command<Successful>() {
    
    override val arguments: List<String>
        get() = listOf("pip", "sync", "pyproject.toml")
    
    override val runningMessage: String
        get() = "Synchronizing..."
    
    override fun run(): Successful {
        return runProcess().checkSuccess(LOGGER)
    }
    
}
