package insyncwithfoo.uv

import com.jetbrains.python.sdk.flavors.CPythonSdkFlavor
import com.jetbrains.python.sdk.flavors.PyFlavorData
import java.io.File


/**
 * @see com.jetbrains.python.sdk.flavors.PythonSdkFlavor
 */
internal object UVSdkFlavor : CPythonSdkFlavor<PyFlavorData.Empty>() {
    
    /**
     * The icon to be used in the interpreter dropdown in
     * **Settings** | **Project** | **Python Interpreter**.
     * 
     * Also used by [UVSdkProvider.getSdkIcon].
     *
     * Size: 16x16
     */
    override fun getIcon() = UVIcon.TINY
    
    override fun getFlavorDataClass(): Class<PyFlavorData.Empty> = PyFlavorData.Empty::class.java
    
    override fun isValidSdkPath(file: File) = false
    
}
