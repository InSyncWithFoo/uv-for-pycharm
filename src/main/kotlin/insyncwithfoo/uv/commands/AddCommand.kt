package insyncwithfoo.uv.commands

import com.jetbrains.python.packaging.common.PythonPackageSpecification
import java.nio.file.Path


internal open class AddCommand(
    override val executable: Path,
    override val workingDirectory: Path,
    private val target: PythonPackageSpecification
) : Command<Successful>() {
    
    override val arguments: List<String>
        get() = listOf("add", target.run { name + versionSpecs.orEmpty() })
    
    override val runningMessage: String
        get() = "Adding ${target}..."
    
    override fun run(): Successful {
        return runProcess().checkSuccess(LOGGER)
    }
    
}
