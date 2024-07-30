package insyncwithfoo.uv.intentions

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.jetbrains.python.codeInsight.intentions.PyBaseIntentionAction
import insyncwithfoo.uv.message
import insyncwithfoo.uv.runBackgroundableTask
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.uv


private fun noExecutableFound() {
    somethingIsWrong(message("messages.noExecutableFound.body"))
}


private fun FileDocumentManager.savePyProjectToml() {
    unsavedDocuments.forEach {
        if (getFile(it)?.name == "pyproject.toml") {
            saveDocument(it)
        }
    }
}


// For some reason, only the legacy Task.Backgroundable works properly.
internal fun Project.runSyncCommand() {
    val uv = this.uv ?: return noExecutableFound()
    
    runBackgroundableTask(message("progresses.title.sync")) {
        uv.sync()
    }
}


internal class UVSyncFromFiles : PyBaseIntentionAction(), DumbAware {
    
    override fun startInWriteAction() = true
    
    override fun getFamilyName() = message("intentions.sync.familyName")
    
    override fun getText() = familyName
    
    /**
     * Always return an empty preview,
     * since this intention does not have any effects
     * whatsoever on the file itself.
     *
     * Super implementation only ever unnecessarily calls
     * [invoke] under a read action, which will cause an error
     * since this intention requires write action.
     */
    override fun generatePreview(project: Project, editor: Editor, file: PsiFile): IntentionPreviewInfo {
        return IntentionPreviewInfo.EMPTY
    }
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val fileName = file?.virtualFile?.name
        return fileName == "pyproject.toml" || fileName == "uv.lock"
    }
    
    override fun doInvoke(project: Project, editor: Editor?, file: PsiFile?) {
        val document = editor?.document ?: return
        val documentManager = FileDocumentManager.getInstance()
        
        documentManager.savePyProjectToml()
        documentManager.saveDocumentAsIs(document)
        project.runSyncCommand()
    }
    
}
