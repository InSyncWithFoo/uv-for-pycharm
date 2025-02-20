package insyncwithfoo.uv.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import insyncwithfoo.uv.intentions.runSyncCommand
import insyncwithfoo.uv.message
import insyncwithfoo.uv.somethingIsWrong


internal class UVSync : DumbAwareAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        project.runSyncCommand()
    }
    
    private fun noProjectFound() {
        somethingIsWrong(
            message("messages.noProjectFound.title"),
            message("messages.noProjectFound.body")
        )
    }
    
}
