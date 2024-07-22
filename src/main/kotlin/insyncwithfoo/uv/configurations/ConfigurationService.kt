package insyncwithfoo.uv.configurations

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service


@State(
    name = "insyncwithfoo.uv.ApplicationConfigurations",
    storages = [Storage("uv.xml", roamingType = RoamingType.LOCAL)]
)
@Service(Service.Level.APP)
internal class ConfigurationService : SimplePersistentStateComponent<Configurations>(Configurations()) {
    
    companion object {
        fun getInstance() = service<ConfigurationService>()
    }
    
}


internal val uvConfigurations: Configurations
    get() = ConfigurationService.getInstance().state


internal fun changeUVConfigurations(action: Configurations.() -> Unit) {
    uvConfigurations.apply(action)
}

