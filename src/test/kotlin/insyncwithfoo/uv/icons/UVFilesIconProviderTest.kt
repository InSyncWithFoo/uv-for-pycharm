package insyncwithfoo.uv.icons

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import junit.framework.TestCase
import javax.swing.Icon


class UVFilesIconProviderTest : TestCase() {
    
    fun `test getIcon - uv-lock`() {
        doTest(LightVirtualFile("uv.lock"), UVIcon.TINY_SIMPLIFIED_WHITE)
    }
    
    fun `test getIcon - uv-toml`() {
        doTest(LightVirtualFile("uv.toml"), UVIcon.TINY_SIMPLIFIED)
    }
    
    fun `test getIcon - pyproject-toml`() {
        doTest(LightVirtualFile("pyproject.toml"), null)
    }
    
    fun `test getIcon - poetry-lock`() {
        doTest(LightVirtualFile("poetry.lock"), null)
    }
    
    fun `test getIcon - uv-py`() {
        doTest(LightVirtualFile("uv.py"), null)
    }
    
    fun `test getIcon - uv-lock-foo`() {
        doTest(LightVirtualFile("uv.lock.foo"), null)
    }
    
    fun `test getIcon - uv-toml-bar`() {
        doTest(LightVirtualFile("uv.toml.bar"), null)
    }
    
    private fun doTest(file: VirtualFile, expected: Icon?) {
        assertEquals(expected, UVFilesIconProvider().getIcon(file, 0, null))
    }
    
}
