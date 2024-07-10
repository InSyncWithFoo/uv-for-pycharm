package insyncwithfoo.uv

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.UserDataHolder
import com.jetbrains.python.sdk.PyInterpreterInspectionQuickFixData
import com.jetbrains.python.sdk.PySdkProvider
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.add.PyAddNewEnvPanel
import org.jdom.Element


/**
 * Responsible for various actions that [PySdkProvider] supports.
 * 
 * The super class is theoretically deprecated/obsolete.
 * Regardless, there have yet to be a clean replacement.
 */
@Suppress("UnstableApiUsage")
internal class UVSdkProvider : PySdkProvider {
    
    // TODO: Implement this
    override fun createEnvironmentAssociationFix(
        module: Module,
        sdk: Sdk,
        isPyCharm: Boolean,
        associatedModulePath: String?
    ): PyInterpreterInspectionQuickFixData? {
        return null
    }
    
    // TODO: Implement this
    override fun createInstallPackagesQuickFix(module: Module): LocalQuickFix? {
        val sdk = PythonSdkUtil.findPythonSdk(module) ?: return null
        
        return when {
            sdk.isUV -> UVInstallQuickFix()
            else -> null
        }
    }
    
    // TODO: Implement this
    override fun createNewEnvironmentPanel(
        project: Project?,
        module: Module?,
        existingSdks: List<Sdk>,
        newProjectPath: String?,
        context: UserDataHolder
    ): PyAddNewEnvPanel {
        TODO("Not yet implemented")
    }
    
    /**
     * The string to be appended to the suggested SDK name
     * when displayed in the status bar.
     * 
     * For example: A default `venv` environments may have
     * its rendered label read `Python 3.12 (project)`.
     * If this method of it were to return `sdk.versionString`,
     * it would read `Python 3.12 (project) [Python 3.12.4]`.
     * 
     * @see com.jetbrains.python.sdk.name
     */
    override fun getSdkAdditionalText(sdk: Sdk) = null
    
    /**
     * The icon to be used in the *Python Interpreter* popup,
     * which is triggered by clicking the corresponding status bar cell.
     * 
     * Size: 16x16
     * 
     * @see com.jetbrains.python.sdk.icon
     */
    override fun getSdkIcon(sdk: Sdk) = UVSdkFlavor.icon.takeIf { sdk.isUV }
    
    override fun loadAdditionalDataForSdk(element: Element) =
        UVSdkAdditionalData.load(element)
    
    override fun tryCreatePackageManagementServiceForSdk(project: Project, sdk: Sdk) = null
    
}
