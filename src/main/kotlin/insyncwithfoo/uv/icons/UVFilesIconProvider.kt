package insyncwithfoo.uv.icons

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile


/**
 * Provides customized directory-tree icons
 * for `uv.lock` and `uv.toml.
 */
internal class UVFilesIconProvider : FileIconProvider, DumbAware {
    
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?) = when (file.name) {
        "uv.lock" -> UVIcon.TINY_SIMPLIFIED_WHITE
        "uv.toml" -> UVIcon.TINY_SIMPLIFIED
        else -> null
    }
    
}
