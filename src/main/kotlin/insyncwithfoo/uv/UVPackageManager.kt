package insyncwithfoo.uv

import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VfsUtil
import com.jetbrains.python.packaging.common.PythonPackage
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.pip.PipRepositoryManager
import com.jetbrains.python.sdk.PythonSdkUpdater
import insyncwithfoo.uv.commands.UV
import insyncwithfoo.uv.commands.UVReportedError


/**
 * This class's methods get called when corresponding functionalities
 * of the *Python Packages* toolwindow are used.
 */
@Suppress("UnstableApiUsage")
internal class UVPackageManager(project: Project, sdk: Sdk) : PythonPackageManager(project, sdk) {
    
    private val uv by lazy { UV.createForProject(project)!! }
    
    override var installedPackages: List<PythonPackage> = emptyList()
        private set
    
    override val repositoryManager by lazy { PipRepositoryManager(project, sdk) }
    
    override suspend fun reloadPackages(): Result<List<PythonPackage>> {
        return when (val output = uv.list()) {
            null -> Result.failure(UVReportedError())
            else -> Result.success(output).also { installedPackages = output }
        }
    }
    
    /**
     * @see com.jetbrains.python.packaging.management.PythonPackageManager.refreshPaths
     */
    private suspend fun refreshPaths() {
        writeAction {
            val (async, recursive, reloadChildren) = Triple(true, true, true)
            
            VfsUtil.markDirtyAndRefresh(
                async, recursive, reloadChildren,
                *sdk.rootProvider.getFiles(OrderRootType.CLASSES)
            )
            PythonSdkUpdater.scheduleUpdate(sdk, project)
        }
    }
    
    private suspend fun refreshAndReload(): Result<List<PythonPackage>> {
        refreshPaths()
        return reloadPackages()
    }
    
    override suspend fun installPackage(specification: PythonPackageSpecification): Result<List<PythonPackage>> {
        return when (uv.add(specification)) {
            false -> Result.failure(UVReportedError())
            true -> refreshAndReload()
        }
    }
    
    override suspend fun updatePackage(specification: PythonPackageSpecification): Result<List<PythonPackage>> {
        return when (uv.update(specification)) {
            false -> Result.failure(UVReportedError())
            true -> refreshAndReload()
        }
    }
    
    override suspend fun uninstallPackage(pkg: PythonPackage): Result<List<PythonPackage>> {
        return when (uv.remove(pkg.name)) {
            false -> Result.failure(UVReportedError())
            true -> refreshAndReload()
        }
    }
    
}
