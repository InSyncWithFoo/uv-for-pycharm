package insyncwithfoo.uv

import com.jetbrains.python.sdk.flavors.PythonFlavorProvider
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor


/**
 * Exists to provide [UVSdkFlavor].
 * 
 * * Usefulness: Undetermined
 * * Implementation: Complete
 */
internal class UVFlavorProvider : PythonFlavorProvider {
    
    override fun getFlavor(platformIndependent: Boolean): PythonSdkFlavor<*> {
        return UVSdkFlavor
    }
    
}
