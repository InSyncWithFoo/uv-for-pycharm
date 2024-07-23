package insyncwithfoo.uv.inspections

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import insyncwithfoo.uv.UVIcon
import insyncwithfoo.uv.message
import java.util.function.Function


internal class UVLockEditingNotice : EditorNotificationProvider, DumbAware {
    
    @Suppress("DialogTitleCapitalization")
    private fun createNotificationPanel() = EditorNotificationPanel().apply {
        text(message("inspections.uvLockEdit"))
        icon(UVIcon.TINY_SIMPLIFIED_WHITE)
    }
    
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<FileEditor, EditorNotificationPanel>? =
        when {
            file.name != "uv.lock" || !file.isWritable -> null
            else -> Function { createNotificationPanel() }
        }
    
}
