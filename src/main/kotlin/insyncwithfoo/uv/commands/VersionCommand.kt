package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class VersionCommand(override val executable: Path) : Command<Version>() {
    
    override val arguments: List<String>
        get() = listOf("--version")
    
    override fun run(): Version {
        return runProcess().stdout
    }
    
}
