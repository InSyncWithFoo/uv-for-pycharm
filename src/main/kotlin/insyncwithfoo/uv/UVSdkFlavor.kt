package insyncwithfoo.uv

import com.jetbrains.python.sdk.flavors.CPythonSdkFlavor
import com.jetbrains.python.sdk.flavors.PyFlavorData
import java.io.File


/**
 * * Usefulness: Somewhat known
 * * Implementation: Potentially incomplete
 */
internal object UVSdkFlavor : CPythonSdkFlavor<PyFlavorData.Empty>() {
    
    override fun getIcon() = UVIcon.TINY
    
    override fun getFlavorDataClass(): Class<PyFlavorData.Empty> = PyFlavorData.Empty::class.java
    
    override fun isValidSdkPath(file: File) = false
    
}
