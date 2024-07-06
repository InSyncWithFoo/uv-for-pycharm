package insyncwithfoo.uv.commands

import com.jetbrains.python.packaging.common.PythonPackageSpecification
import java.nio.file.Path


internal class UpgradeCommand(executable: Path, workingDirectory: Path, specification: PythonPackageSpecification) :
    AddCommand(executable, workingDirectory, specification) {
    
    override val arguments: List<String>
        get() = super.arguments + "--upgrade"
    
}
