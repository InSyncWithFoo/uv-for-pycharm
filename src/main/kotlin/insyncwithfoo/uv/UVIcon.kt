package insyncwithfoo.uv

import com.intellij.openapi.util.IconLoader


private fun loadIcon(path: String) = IconLoader.getIcon(path, UVIcon::class.java)


@Suppress("unused")
internal object UVIcon {
    val BIG by lazy { loadIcon("icons/big.svg") }
    val MEDIUM by lazy { loadIcon("icons/medium.svg") }
    val SMALL by lazy { loadIcon("icons/small.svg") }
}
