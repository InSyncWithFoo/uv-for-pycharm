package insyncwithfoo.uv.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.jetbrains.python.packaging.management.PythonPackageManager
import insyncwithfoo.uv.message
import insyncwithfoo.uv.runInBackground
import insyncwithfoo.uv.sdk
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.uv


internal class PipSync : DumbAwareAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        
        project.runSyncCommand()
        project.reloadPackages()
    }
    
    private fun Project.runSyncCommand() {
        val uv = uv ?: return noExecutableFound()
        
        runInBackground("Synchronizing...") {
            uv.sync()
        }
    }
    
    @Suppress("UnstableApiUsage")
    private fun Project.reloadPackages() {
        val sdk = this.sdk ?: return
        val uvPackageManager = PythonPackageManager.forSdk(this, sdk)
        
        runInBackground("Reload packages") {
            uvPackageManager.reloadPackages()
        }
    }
    
    private fun noProjectFound() {
        somethingIsWrong(
            message("messages.noProjectFound.title"),
            message("messages.noProjectFound.body")
        )
    }
    
    private fun noExecutableFound() {
        somethingIsWrong(message("messages.noExecutableFound.body"))
    }
    
}
