package insyncwithfoo.uv.generator

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.platform.DirectoryProjectGenerator
import com.jetbrains.python.newProject.PythonProjectGenerator
import insyncwithfoo.uv.UVIcon
import insyncwithfoo.uv.message


internal class UVProjectGenerator : PythonProjectGenerator<Settings>(), CustomStepProjectGenerator<Settings> {
    
    override fun getName() = message("newProjectPanel.title")
    
    override fun getLogo() = UVIcon.SMALL
    
    override fun getProjectSettings() = Settings()
    
    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<Settings>?,
        callback: AbstractCallback<Settings>?
    ) =
        UVProjectSettingsStep(this)
    
}
