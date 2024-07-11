package insyncwithfoo.uv

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.TaskCancellation
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.ide.progress.withModalProgress
import insyncwithfoo.uv.commands.ProjectUV
import insyncwithfoo.uv.commands.UV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.nio.file.Path


private val <T> Array<T>.onlyElement: T?
    get() = firstOrNull().takeIf { size == 1 }


private val Project.modules: Array<Module>
    get() = moduleManager.modules


private val Project.modalTaskOwner: ModalTaskOwner
    get() = ModalTaskOwner.project(this)


internal val Project.uv: ProjectUV?
    get() = UV.createForProject(this)


internal val Project.path: Path?
    get() = basePath?.let { Path.of(it) }


internal val Project.sdk: Sdk?
    get() = ProjectRootManager.getInstance(this).projectSdk


internal val Project.interpreterPath: Path?
    get() = sdk?.homePath?.let { Path.of(it) }


internal val Project.moduleManager: ModuleManager
    get() = ModuleManager.getInstance(this)


internal val Project.isNormal: Boolean
    get() = !this.isDefault && !this.isDisposed


internal val Project.onlyModuleOrNull: Module?
    get() = modules.onlyElement


internal val Project.hasOnlyOneModule: Boolean
    get() = modules.size == 1


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

