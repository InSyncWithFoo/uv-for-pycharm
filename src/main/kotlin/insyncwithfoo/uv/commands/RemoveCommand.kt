package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class RemoveCommand(
    override val executable: Path,
    override val workingDirectory: Path,
    private val target: String
) : Command<Successful>() {
    
    override val arguments: List<String>
        get() = listOf("remove", target)
    
    override val runningMessage: String
        get() = "Removing $target..."
    
    override fun run(): Successful {
        return runProcess().checkSuccess(LOGGER)
    }
    
}
