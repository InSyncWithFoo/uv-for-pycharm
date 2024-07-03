package insyncwithfoo.uv

import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries


private fun Path.isEmpty() = listDirectoryEntries().isEmpty()


internal fun Path.isNonEmptyDirectory() = isDirectory() && !isEmpty()
