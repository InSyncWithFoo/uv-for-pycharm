package insyncwithfoo.uv

import com.jetbrains.python.sdk.flavors.PythonFlavorProvider
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor


/**
 * Exists only to provide [UVSdkFlavor].
 */
internal class UVFlavorProvider : PythonFlavorProvider {
    
    override fun getFlavor(platformIndependent: Boolean): PythonSdkFlavor<*> {
        return UVSdkFlavor
    }
    
}
