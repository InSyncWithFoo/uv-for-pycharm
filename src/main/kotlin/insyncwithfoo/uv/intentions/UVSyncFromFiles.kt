package insyncwithfoo.uv.intentions

import com.intellij.openapi.application.writeAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.jetbrains.python.codeInsight.intentions.PyBaseIntentionAction
import com.jetbrains.python.packaging.management.PythonPackageManager
import insyncwithfoo.uv.message
import insyncwithfoo.uv.runInBackground
import insyncwithfoo.uv.sdk
import insyncwithfoo.uv.somethingIsWrong
import insyncwithfoo.uv.uv


private fun noExecutableFound() {
    somethingIsWrong(message("messages.noExecutableFound.body"))
}


private fun Project.runSyncCommand() {
    val uv = this.uv ?: return noExecutableFound()
    
    @Suppress("UnstableApiUsage")
    runInBackground("Synchronizing...") {
        writeAction { uv.sync() }
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


internal fun Project.syncThenReload() {
    runSyncCommand()
    reloadPackages()
}


internal class UVSyncFromFiles : PyBaseIntentionAction() {
    
    override fun startInWriteAction() = true
    
    override fun getFamilyName() = message("intentions.sync.familyName")
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val fileName = file?.virtualFile?.name
        return fileName == "pyproject.toml" || fileName == "uv.lock"
    }
    
    override fun doInvoke(project: Project, editor: Editor?, file: PsiFile?) {
        project.syncThenReload()
    }
    
}
