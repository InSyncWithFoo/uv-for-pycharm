package insyncwithfoo.uv

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.sdk.PythonSdkAdditionalData
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor
import java.nio.file.Path


internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()


// TODO: Check this
// TODO: Figure out how to use alternative constructors of PythonSdkAdditionalData
internal var Sdk.isUV: Boolean
    get() = sdkAdditionalData is UVSdkAdditionalData
    set(enabled) {
        val modificator = sdkModificator
        val oldData = sdkAdditionalData
        
        modificator.sdkAdditionalData = when {
            enabled -> UVSdkAdditionalData(oldData as? PythonSdkAdditionalData)
            oldData is UVSdkAdditionalData -> PythonSdkAdditionalData(PythonSdkFlavor.getFlavor(this))
            else -> oldData
        }
        
        runWriteAction { modificator.commitChanges() }
    }
