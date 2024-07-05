package insyncwithfoo.uv

import com.jetbrains.python.sdk.PythonSdkAdditionalData
import com.jetbrains.python.sdk.flavors.PyFlavorAndData
import com.jetbrains.python.sdk.flavors.PyFlavorData
import org.jdom.Element


internal class UVSdkAdditionalData : PythonSdkAdditionalData {
    
    constructor() : super(PyFlavorAndData(PyFlavorData.Empty, UVSdkFlavor))
    constructor(data: PythonSdkAdditionalData? = null) : super(data ?: PythonSdkAdditionalData())
    
    override fun save(element: Element) {
        super.save(element)
        element.setAttribute(IS_UV, "true")
    }
    
    companion object {
        private const val IS_UV = "IS_UV"
        
        fun load(element: Element) = when {
            element.getAttributeValue(IS_UV) != "true" -> null
            else -> UVSdkAdditionalData().apply { load(element) }
        }
    }
    
}
