package insyncwithfoo.uv

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import com.jetbrains.python.packaging.common.PythonSimplePackageSpecification
import insyncwithfoo.uv.commands.UV


private typealias PackageName = String


@Suppress("UnstableApiUsage")
private fun PackageName.toSpecification(): PythonPackageSpecification {
    return PythonSimplePackageSpecification(name = this, version = null, repository = null)
}


internal class UVInstallQuickFix : LocalQuickFix {
    
    override fun getFamilyName() = message("intentions.installQuickFix.familyName")
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val packageName = descriptor.psiElement.text
        val uv = UV.createForProject(project)
        
        uv?.add(packageName.toSpecification()) ?: return
    }
    
}
