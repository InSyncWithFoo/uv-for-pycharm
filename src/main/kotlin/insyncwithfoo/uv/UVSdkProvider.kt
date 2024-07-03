package insyncwithfoo.uv

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.UserDataHolder
import com.jetbrains.python.sdk.PyInterpreterInspectionQuickFixData
import com.jetbrains.python.sdk.PySdkProvider
import com.jetbrains.python.sdk.add.PyAddNewEnvPanel
import org.jdom.Element


@Suppress("UnstableApiUsage")
internal class UVSdkProvider : PySdkProvider {
    
    override fun createEnvironmentAssociationFix(
        module: Module,
        sdk: Sdk,
        isPyCharm: Boolean,
        associatedModulePath: String?
    ): PyInterpreterInspectionQuickFixData? {
        TODO("Not yet implemented")
    }
    
    // TODO: Implement this
    override fun createInstallPackagesQuickFix(module: Module): LocalQuickFix? {
        return null
    }
    
    override fun createNewEnvironmentPanel(
        project: Project?,
        module: Module?,
        existingSdks: List<Sdk>,
        newProjectPath: String?,
        context: UserDataHolder
    ): PyAddNewEnvPanel {
        TODO("Not yet implemented")
    }
    
    override fun getSdkAdditionalText(sdk: Sdk) = when {
        sdk.isUV -> sdk.versionString
        else -> null
    }
    
    override fun getSdkIcon(sdk: Sdk) = UVIcon.BIG.takeIf { sdk.isUV }
    
    override fun loadAdditionalDataForSdk(element: Element) =
        UVSdkAdditionalData.load(element)
    
    override fun tryCreatePackageManagementServiceForSdk(project: Project, sdk: Sdk) = null
    
}
