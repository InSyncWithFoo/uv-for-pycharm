package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class SyncCommand(
    override val executable: Path,
    override val workingDirectory: Path
) : Command<Successful>() {
    
    override val arguments: List<String>
        get() = listOf("sync")
    
    override val runningMessage: String
        get() = "Synchronizing..."
    
    override fun run(): Successful {
        return runAndLogProcess().checkSuccess(LOGGER)
    }
    
}
