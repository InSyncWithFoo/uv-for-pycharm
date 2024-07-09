package insyncwithfoo.uv.configurations

import com.intellij.openapi.options.Configurable
import com.intellij.util.xmlb.XmlSerializerUtil
import insyncwithfoo.uv.message


internal class UVConfigurable : Configurable {
    
    private val service = ConfigurationService.getInstance()
    private val state = service.state.copy()
    private val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurationPanel.title")
    
    override fun createComponent() = panel
    
    override fun isModified() = panel.isModified()
    
    override fun apply() {
        panel.apply()
        XmlSerializerUtil.copyBean(state, service.state)
    }
    
    override fun reset() {
        panel.reset()
    }
    
}
