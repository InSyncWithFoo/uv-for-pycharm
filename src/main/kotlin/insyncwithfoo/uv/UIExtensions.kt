package insyncwithfoo.uv

import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.toNullableProperty
import com.jetbrains.python.sdk.add.PySdkPathChoosingComboBox
import com.jetbrains.python.sdk.add.addInterpretersAsync
import com.jetbrains.python.sdk.detectSystemWideSdks
import javax.swing.JComponent
import kotlin.reflect.KMutableProperty0


private fun TextFieldWithBrowseButton.addSingleFileChooser(
    title: String? = null,
    description: String? = null,
    project: Project? = null
) {
    val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
    addBrowseFolderListener(title, description, project, descriptor)
}


private fun PySdkPathChoosingComboBox.addInterpreters(obtainer: () -> List<Sdk>) {
    addInterpretersAsync(this, obtainer)
}


internal fun <T : JComponent> Cell<T>.applyReturningComponent(block: Cell<T>.() -> Unit) =
    this.apply(block).component


internal fun <T : JComponent> Cell<T>.makeFlexible() = apply {
    align(AlignX.FILL)
    resizableColumn()
}


internal fun ValidationResult.toInfo() =
    errorMessage?.let { ValidationInfo(it) }


internal fun <T : JComponent, V> Cell<T>.bind(property: MutableProperty<V>) {
    val getter: (T) -> V = { _ -> property.get() }
    val setter: (T, V) -> Unit = { _, value -> property.set(value) }
    
    bind(getter, setter, property)
}


internal fun <T : JComponent, V> Cell<T>.bind(property: KMutableProperty0<V?>) {
    bind(property.toNullableProperty(defaultValue = null))
}


internal fun Row.reactiveLabel(property: ObservableMutableProperty<String>, maxLineLength: Int = 60) =
    comment("", maxLineLength).bindText(property)


internal fun Row.singlePathTextField() =
    textFieldWithBrowseButton().apply { component.addSingleFileChooser() }


internal fun PySdkPathChoosingComboBox.addSystemWideInterpreters() {
    addInterpreters { detectSystemWideSdks(module = null, existingSdks = emptyList()) }
}
