package insyncwithfoo.uv

import com.intellij.openapi.projectRoots.Sdk
import java.nio.file.Path


internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()


internal val Sdk.isUV: Boolean
    get() = sdkAdditionalData is UVSdkAdditionalData
