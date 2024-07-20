package insyncwithfoo.uv

import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries


private val Path.isEmpty: Boolean
    get() = this.toString() == ""


private fun Path.directoryIsEmpty() = listDirectoryEntries().isEmpty()


/**
 * Parse the string and return a [Path]
 * if the new [Path] is not blank or invalid.
 */
internal fun String.toPathOrNull() =
    try {
        Path.of(this).takeUnless { it.isEmpty }
    } catch (_: InvalidPathException) {
        null
    }


internal fun String.toPathIfItExists(base: Path? = Path.of("")) =
    this.toPathOrNull()
        ?.let { (base?.resolve(it) ?: it).normalize() }
        ?.takeIf { it.exists() }


internal fun Path.resolvedAgainst(base: Path?) =
    base?.resolve(this) ?: this


internal fun Path.isNonEmptyDirectory() = isDirectory() && !directoryIsEmpty()
