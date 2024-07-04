package insyncwithfoo.uv.commands

import com.jetbrains.python.packaging.common.PythonPackageSpecification
import java.nio.file.Path


internal class AddCommand(
    override val executable: Path,
    override val workingDirectory: Path?,
    private val target: PythonPackageSpecification
) : Command() {
    
    override val arguments: List<String>
        get() = TODO("Not yet implemented")
    
    override val runningMessage: String
        get() = "Adding ${target.versionSpecs}..."
    
}
