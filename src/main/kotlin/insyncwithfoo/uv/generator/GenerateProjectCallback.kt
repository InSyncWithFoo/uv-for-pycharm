package insyncwithfoo.uv.generator

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.ProjectGeneratorPeer
import com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep
import insyncwithfoo.uv.moduleManager
import insyncwithfoo.uv.rootManager
import insyncwithfoo.uv.uv


private fun UVProjectGenerator.makeSettings(settingsStep: UVProjectSettingsStep) =
    projectSettings.apply {
        sdk = settingsStep.sdk
        interpreterInfoForStatistics = settingsStep.interpreterInfoForStatistics
    }


private fun UVProjectGenerator.generateProject(settingsStep: UVProjectSettingsStep, settings: Settings) : Project? {
    val location = FileUtil.expandUserHome(settingsStep.projectLocation)
    return AbstractNewProjectStep.doGenerateProject(null, location, this, settings)
}


// TODO: Use `uv init` instead
private fun Project.initializeUsingUV() {
    this.uv!!.init()
}


private fun Project.initializeGit() {
    val module = moduleManager.modules.firstOrNull() ?: return
    val moduleRoot = module.rootManager.contentRoots.firstOrNull() ?: return
    
    PythonProjectSpecificSettingsStep.initializeGit(this, moduleRoot)
}


/**
 * A reimplementation of `PythonGenerateProjectCallback`
 * (an `impl` class, and thus unlinkable in KDoc)
 * with many code paths removed or rewritten.
 * 
 * It is responsible for calling SDK-creating code
 * as well as initializing Git repository if necessary.
 */
internal class GenerateProjectCallback : AbstractCallback<Settings>() {
    
    override fun consume(settingsStep: ProjectSettingsStepBase<Settings>?, peer: ProjectGeneratorPeer<Settings>) {
        val generator = (settingsStep as UVProjectSettingsStep).projectGenerator as UVProjectGenerator
        
        val settings = generator.makeSettings(settingsStep)
        val newProject = generator.generateProject(settingsStep, settings) ?: error("Failed to generate project")
        
        val sdk = settings.sdk
        
        SdkConfigurationUtil.setDirectoryProjectSdk(newProject, sdk)
        
        newProject.initializeUsingUV()
        
        if (settingsStep.initializeGit.get()) {
            newProject.initializeGit()
        }
    }
    
}
