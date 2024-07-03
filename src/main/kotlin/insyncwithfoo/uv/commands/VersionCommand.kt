package insyncwithfoo.uv.commands

import java.nio.file.Path


internal class VersionCommand(override val executable: Path) : Command() {
    override val arguments: List<String>
        get() = listOf("--version")
}
