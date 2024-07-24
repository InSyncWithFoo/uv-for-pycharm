package insyncwithfoo.uv

import com.intellij.openapi.util.IconLoader


private fun loadIcon(path: String) = IconLoader.getIcon(path, UVIcon::class.java)


@Suppress("unused")
internal object UVIcon {
    val BIG by lazy { loadIcon("icons/330.svg") }
    val MEDIUM by lazy { loadIcon("icons/100.svg") }
    val SMALL by lazy { loadIcon("icons/18.svg") }
    val TINY by lazy { loadIcon("icons/16.svg") }
    
    val SMALL_SIMPLIFIED by lazy { loadIcon("icons/18-simplified.svg") }
    val TINY_SIMPLIFIED by lazy { loadIcon("icons/16-simplified.svg") }
    val TINY_SIMPLIFIED_WHITE by lazy { loadIcon("icons/16-simplified-white.svg") }
}
