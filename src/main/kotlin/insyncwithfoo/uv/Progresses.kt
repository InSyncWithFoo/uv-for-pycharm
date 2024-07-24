package insyncwithfoo.uv

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.TaskCancellation
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.ide.progress.withModalProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking


private val Project.modalTaskOwner: ModalTaskOwner
    get() = ModalTaskOwner.project(this)


private fun Task.Backgroundable.run() {
    ProgressManager.getInstance().run(this)
}


private fun Project.backgroundableTask(title: String, action: () -> Unit) =
    object : Task.Backgroundable(this, title, false) {
        override fun run(indicator: ProgressIndicator) {
            indicator.text = title
            indicator.isIndeterminate = true
            action()
        }
    }


internal fun Project.runBackgroundableTask(title: String, action: () -> Unit) {
    backgroundableTask(title, action).run()
}


internal fun <T> Project.runInBackground(
    title: String,
    cancellable: Boolean = false,
    action: suspend CoroutineScope.() -> T
) = runBlocking {
    withBackgroundProgress(this@runInBackground, title, cancellable, action)
}


internal fun <T> Project.runInForeground(
    title: String,
    cancellation: TaskCancellation = TaskCancellation.nonCancellable(),
    action: suspend CoroutineScope.() -> T
) = runBlocking {
    withModalProgress(this@runInForeground.modalTaskOwner, title, cancellation, action)
}
