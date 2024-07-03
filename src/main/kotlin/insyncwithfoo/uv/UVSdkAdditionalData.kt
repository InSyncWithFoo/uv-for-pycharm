package insyncwithfoo.uv

import com.jetbrains.python.sdk.PythonSdkAdditionalData
import org.jdom.Element


internal class UVSdkAdditionalData(data: PythonSdkAdditionalData? = null) :
    PythonSdkAdditionalData(data ?: PythonSdkAdditionalData()) {
    
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
        
        fun copy(data: PythonSdkAdditionalData): UVSdkAdditionalData =
            UVSdkAdditionalData(data)
    }
    
}
