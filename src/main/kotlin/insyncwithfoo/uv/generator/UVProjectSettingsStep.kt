package insyncwithfoo.uv.generator

import com.intellij.ide.IdeBundle
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.bind
import com.intellij.openapi.observable.util.bindBooleanStorage
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.PyBundle
import com.jetbrains.python.PySdkBundle
import com.jetbrains.python.newProject.steps.ProjectSpecificSettingsStep
import com.jetbrains.python.sdk.PyLazySdk
import com.jetbrains.python.sdk.add.PySdkComboBoxItem
import com.jetbrains.python.sdk.add.PySdkPathChoosingComboBox
import insyncwithfoo.uv.addSystemWideInterpreters
import insyncwithfoo.uv.applyReturningComponent
import insyncwithfoo.uv.commands.UV
import insyncwithfoo.uv.isNonEmptyDirectory
import insyncwithfoo.uv.makeFlexible
import insyncwithfoo.uv.message
import insyncwithfoo.uv.reactiveLabel
import insyncwithfoo.uv.singlePathTextField
import insyncwithfoo.uv.toInfo
import insyncwithfoo.uv.toPathOrNull
import org.jetbrains.annotations.Nls
import java.nio.file.InvalidPathException
import java.nio.file.Path
import javax.swing.JPanel
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable
import kotlin.io.path.isRegularFile


private fun <T> GraphProperty<T>.dependsOn(vararg parents: ObservableProperty<*>, update: () -> T) {
    parents.forEach { parent ->
        dependsOn(parent, update)
    }
}


private fun Panel.rowWithTopGap(label: @Nls String, init: Row.() -> Unit) {
    row(label, init).topGap(TopGap.MEDIUM)
}


private fun UVProjectSettingsStep.setErrorTextReturningValidity(text: String?): Boolean {
    setErrorText(text)
    return text == null
}


/**
 * @see com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep
 */
internal class UVProjectSettingsStep(projectGenerator: DirectoryProjectGenerator<Settings>) :
    ProjectSpecificSettingsStep<Settings>(projectGenerator, GenerateProjectCallback()), DumbAware {
    
    private val propertyGraph = PropertyGraph()
    
    private val projectName = propertyGraph.property("")
    private val projectLocation = propertyGraph.property("")
    
    private val projectPath: Path?
        get() = try {
            Path.of(projectLocation.get(), projectName.get())
        } catch (_: InvalidPathException) {
            null
        }
    
    private val projectPathHint = propertyGraph.property("").apply {
        dependsOn(projectName, projectLocation) {
            val projectPath = projectPath
            
            when {
                projectPath == null -> message("newProjectPanel.hint.invalidPath")
                projectPath.isRegularFile() -> message("newProjectPanel.hint.existingFile")
                projectPath.isNonEmptyDirectory() -> message("newProjectPanel.hint.nonEmptyDirectory")
                else -> PyBundle.message("new.project.location.hint", projectPath)
            }
        }
    }
    
    private val projectPathIsValid = propertyGraph.property(false).apply {
        dependsOn(projectPathHint) {
            projectPathHint.get() == PyBundle.message("new.project.location.hint", projectPath)
        }
    }
    
    private val baseInterpreter = propertyGraph.property<PySdkComboBoxItem?>(null)
    
    private val baseInterpreterIsValid = propertyGraph.property(false).apply {
        dependsOn(baseInterpreter) {
            baseInterpreter.get() != null
        }
    }
    
    private val uvExecutable = propertyGraph.property("")
    
    private val uvExecutablePathHint = propertyGraph.property("").apply {
        dependsOn(uvExecutable) {
            val path = uvExecutable.get().toPathOrNull()
            
            when {
                path == null -> message("newProjectPanel.hint.invalidPath")
                !path.isAbsolute -> message("newProjectPanel.hint.nonAbsolutePath")
                !path.exists() -> message("newProjectPanel.hint.notFound")
                path.isDirectory() -> message("newProjectPanel.hint.unexpectedDirectory")
                !path.isExecutable() -> message("newProjectPanel.hint.nonExecutable")
                else -> message("newProjectPanel.hint.fileFound")
            }
        }
    }
    
    private val uvExecutablePathIsValid = propertyGraph.property(false).apply {
        dependsOn(uvExecutablePathHint) {
            uvExecutablePathHint.get() == message("newProjectPanel.hint.fileFound")
        }
    }
    
    internal val initializeGit = propertyGraph.property(false)
        .bindBooleanStorage("PyCharm.NewProject.Git")
    
    
    private lateinit var projectNameInput: JBTextField
    private var projectLocationInput by ::myLocationField
    private lateinit var baseInterpreterInput: PySdkPathChoosingComboBox
    private lateinit var uvExecutableInput: TextFieldWithBrowseButton
    
    /**
     * Generates a name for the new project (and its own directory).
     */
    private val nextProjectDirectory by ::myProjectDirectory
    
    private val venvCreator: VenvCreator
        get() = VenvCreator(
            executable = uvExecutable.get().toPathOrNull()!!,
            projectPath = projectPath!!,
            baseSdk = baseInterpreterInput.selectedSdk!!
        )
    
    /**
     * Whether the example "main.py" script should be created.
     * 
     * Always return `false` to reduce maintenance burden.
     */
    override fun createWelcomeScript() = false
    
    override fun getProjectLocation() =
        FileUtil.expandUserHome(projectLocation.joinCanonicalPath(projectName).get())
    
    override fun getRemotePath() = null
    
    override fun createBasePanel(): JPanel {
        val panel = recreateProjectCreationPanel()
        val properties = listOf(projectName, projectLocation, baseInterpreter, uvExecutable)
        
        setNewProjectName()
        actionButton.addActionListener { panel.apply() }
        properties.forEach { it.afterChange { checkValid() } }
        
        return panel
    }
    
    private fun setNewProjectName() {
        val nextProjectName = nextProjectDirectory.get()
        
        projectName.set(nextProjectName.nameWithoutExtension)
        projectLocation.set(nextProjectName.parent)
    }
    
    /**
     * @see com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep.createBasePanel
     * @see com.jetbrains.python.sdk.add.v2.PythonAddNewEnvironmentPanel
     */
    private fun recreateProjectCreationPanel() = panel {
        row(PyBundle.message("new.project.name")) {
            projectNameInput = textField().applyReturningComponent {
                validationOnInput { projectGenerator.validate(getProjectLocation()).toInfo() }
                bindText(projectName)
            }
        }
        row(PyBundle.message("new.project.location")) {
            projectLocationInput = singlePathTextField().applyReturningComponent {
                makeFlexible()
                bindText(projectLocation)
            }
        }
        row("") {
            reactiveLabel(projectPathHint)
        }
        
        row("") {
            checkBox(PyBundle.message("new.project.git")).bindSelected(initializeGit)
        }
        
        row("") {}
        
        panel {
            rowWithTopGap(PySdkBundle.message("python.venv.base.label")) {
                // TODO: Switch to com.jetbrains.python.sdk.add.v2.pythonInterpreterComboBox
                baseInterpreterInput = cell(PySdkPathChoosingComboBox()).applyReturningComponent {
                    makeFlexible()
                    component.addSystemWideInterpreters()
                    component.childComponent.bind(baseInterpreter)
                }
            }
            
            row(message("newProjectPanel.settings.uvExecutable.label")) {
                uvExecutableInput = singlePathTextField().applyReturningComponent {
                    makeFlexible()
                    bindText(uvExecutable)
                    
                    uvExecutable.set(UV.executable?.toString().orEmpty())
                }
            }
            row("") {
                reactiveLabel(uvExecutablePathHint)
            }
        }
    }
    
    /**
     * Called by [createBasePanel] and various other functions elsewhere.
     *
     * Responsible for setting the error notice
     * (at the bottom of the panel) if necessary.
     */
    override fun checkValid(): Boolean {
        val text = when {
            !projectPathIsValid.get() -> IdeBundle.message("new.dir.project.error.invalid")
            !baseInterpreterIsValid.get() -> message("newProjectPanel.validation.noBaseInterpreter")
            !uvExecutablePathIsValid.get() -> message("newProjectPanel.validation.invalidUVExecutable")
            else -> null
        }
        return setErrorTextReturningValidity(text)
    }
    
    /**
     * Gets called when the panel created by [createBasePanel] is selected.
     *
     * This deliberately does nothing.
     * The super implementation just calls some validation code
     * under a check which we will never pass.
     */
    override fun onPanelSelected() {}
    
    /**
     * Creates the virtual environment and returns the SDK derived from that.
     *
     * @see com.jetbrains.python.sdk.add.v2.setupVirtualenv
     */
    override fun getSdk() = PyLazySdk("Uninitialized environment") {
        val sdk = venvCreator.createSdk() ?: error("Failed to create SDK")
        SdkConfigurationUtil.addSdk(sdk)
        sdk
    }
    
}
