package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class RemoveCommand(
    override val executable: Path,
    private val target: String
) : Command() {
    
    override val arguments: List<String>
        get() = TODO("Not yet implemented")
    
    override val runningMessage: String
        get() = "Removing $target..."
    
}
