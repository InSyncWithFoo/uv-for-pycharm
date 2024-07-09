package insyncwithfoo.uv.configurations

import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.uv.commands.UV
import insyncwithfoo.uv.makeFlexible
import insyncwithfoo.uv.message
import insyncwithfoo.uv.singlePathTextField
import kotlin.reflect.KMutableProperty0


private fun <T> KMutableProperty0<T?>.toNonNullableProperty(getDefaultValue: () -> T): MutableProperty<T> {
    return MutableProperty({ get() ?: getDefaultValue() }, { set(it) })
}


internal fun createPanel(state: Configurations) = panel {
    row(message("configurationPanel.executable.label")) {
        singlePathTextField().apply {
            makeFlexible()
            bindText(state::executable.toNonNullableProperty { UV.detectExecutable()?.toString().orEmpty() })
        }
    }
}
