package insyncwithfoo.uv

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.management.PythonPackageManagerProvider


/**
 * Exists only to provide [UVPackageManager].
 * 
 * * Usefulness: Known
 * * Implementation: Complete
 */
@Suppress("UnstableApiUsage")
internal class UVPackageManagerProvider : PythonPackageManagerProvider {
    
    override fun createPackageManagerForSdk(project: Project, sdk: Sdk): PythonPackageManager? =
        UVPackageManager(project, sdk).takeIf { sdk.isUV }
    
}
