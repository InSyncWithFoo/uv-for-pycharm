package insyncwithfoo.uv.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import junit.framework.TestCase


class UVSyncFromFilesTest : BasePlatformTestCase() {
    
    private val fixture: CodeInsightTestFixture by ::myFixture
    
    override fun getTestDataPath() = "src/test/testData/intentions/UVSyncFromFiles"
    
    fun `test - startInWriteAction`() {
        val intention = UVSyncFromFiles()
        TestCase.assertEquals(intention.startInWriteAction(), true)
    }
    
    fun `test - text`() {
        val intention = UVSyncFromFiles()
        TestCase.assertEquals(intention.familyName, intention.text)
    }
    
    fun `test - pyproject-toml`() {
        doTest("pyproject.toml") { intention ->
            TestCase.assertNotNull(intention)
        }
    }
    
    fun `test - uv-lock`() {
        doTest("uv.lock") { intention ->
            TestCase.assertNotNull(intention)
        }
    }
    
    fun `test - uv-toml`() {
        doTest("uv.toml") { intention ->
            TestCase.assertNull(intention)
        }
    }
    
    fun `test - generatePreview - pyproject-toml`() {
        doGeneratePreviewTest("pyproject.toml")
    }
    
    fun `test - generatePreview - uv-lock`() {
        doGeneratePreviewTest("uv.lock")
    }
    
    fun `test - generatePreview - uv-toml`() {
        doGeneratePreviewTest("uv.toml")
    }
    
    private fun doTest(file: String, check: (IntentionAction?) -> Unit) {
        fixture.configureByFile(file)
        
        val hint = UVSyncFromFiles().text
        val action = fixture.filterAvailableIntentions(hint).run {
            firstOrNull().takeIf { size == 1 }
        }
        
        check(action)
    }
    
    private fun doGeneratePreviewTest(fileName: String) {
        val intention = UVSyncFromFiles()
        val file = LightVirtualFile(fileName)
        
        fixture.openFileInEditor(file)
        
        val (project, editor, psiFile) = fixture.run {
            Triple(project, editor, psiManager.findFile(file)!!)
        }
        
        TestCase.assertEquals(
            intention.generatePreview(project, editor, psiFile),
            IntentionPreviewInfo.EMPTY
        )
    }
    
}
