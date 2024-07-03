package insyncwithfoo.uv

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.common.PythonPackage
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.management.PythonRepositoryManager
import com.jetbrains.python.packaging.pip.PipRepositoryManager


/**
 * The methods get called when corresponding functionalities
 * of the "Python packages" window are used.
 * 
 * * Usefulness: Known
 * * Implementation: Incomplete
 */
@Suppress("UnstableApiUsage")
internal class UVPackageManager(project: Project, sdk: Sdk) : PythonPackageManager(project, sdk) {
    
    override val installedPackages: List<PythonPackage>
        get() = TODO("Not yet implemented")
    
    override val repositoryManager: PythonRepositoryManager
        get() = PipRepositoryManager(project, sdk)
    
    override suspend fun installPackage(specification: PythonPackageSpecification): Result<List<PythonPackage>> {
        TODO("Not yet implemented")
    }
    
    override suspend fun reloadPackages(): Result<List<PythonPackage>> {
        TODO("Not yet implemented")
    }
    
    override suspend fun uninstallPackage(pkg: PythonPackage): Result<List<PythonPackage>> {
        TODO("Not yet implemented")
    }
    
    override suspend fun updatePackage(specification: PythonPackageSpecification): Result<List<PythonPackage>> {
        TODO("Not yet implemented")
    }
    
}
