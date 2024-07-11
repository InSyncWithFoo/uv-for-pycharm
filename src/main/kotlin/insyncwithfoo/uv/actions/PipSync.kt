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
        
        runSyncCommand(project)
        reloadPackages(project)
    }
    
    private fun runSyncCommand(project: Project) {
        val uv = project.uv ?: return noExecutableFound()
        
        project.runInBackground("Synchronizing...") {
            uv.sync()
        }
    }
    
    @Suppress("UnstableApiUsage")
    private fun reloadPackages(project: Project) {
        val sdk = project.sdk ?: return
        
        project.runInBackground("Reload packages") {
            PythonPackageManager.forSdk(project, sdk).reloadPackages()
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
