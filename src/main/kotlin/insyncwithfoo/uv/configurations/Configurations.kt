package insyncwithfoo.uv.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.util.xmlb.XmlSerializerUtil


internal class Configurations : BaseState() {
    var executable by string(null)
}


internal fun Configurations.copy(): Configurations {
    return XmlSerializerUtil.createCopy(this)
}
