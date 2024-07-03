package insyncwithfoo.uv

import com.intellij.execution.target.TargetEnvironmentConfiguration
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.sdk.flavors.CPythonSdkFlavor
import com.jetbrains.python.sdk.flavors.PyFlavorData
import java.io.File


/**
 * * Usefulness: Undetermined
 * * Implementation: Potentially incomplete
 */
internal object UVSdkFlavor : CPythonSdkFlavor<PyFlavorData.Empty>() {
    
    override fun getIcon() = UVIcon.BIG
    
    override fun getFlavorDataClass(): Class<PyFlavorData.Empty> = PyFlavorData.Empty::class.java
    
    override fun isValidSdkPath(file: File) = false
    
    override fun sdkSeemsValid(
        sdk: Sdk,
        flavorData: PyFlavorData.Empty,
        targetConfig: TargetEnvironmentConfiguration?
    ): Boolean {
        // val sdkPath = sdk.path
        
        return super.sdkSeemsValid(sdk, flavorData, targetConfig)
    }
    
}
