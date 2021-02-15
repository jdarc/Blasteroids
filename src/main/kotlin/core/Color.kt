@file:Suppress("unused", "DataClassPrivateConstructor", "MemberVisibilityCanBePrivate")

package core

data class Color private constructor(val red: Float, val grn: Float, val blu: Float, val alpha: Float) {

    companion object {
        val BLACK = create(0x000000)
        val WHITE = create(0xFFFFFF)
        val RED = create(0xFF0000)
        val GREEN = create(0x00FF00)
        val BLUE = create(0x0000FF)

        fun create(rgb: Int, alpha: Int = 0xFF): Color {
            val r = 0xFF and rgb.shr(16)
            val g = 0xFF and rgb.shr(8)
            val b = 0xFF and rgb
            return create(r / 255F, g / 255F, b / 255F, alpha / 255F)
        }

        fun create(r: Float, g: Float, b: Float, a: Float = 1F) =
            Color(r.coerceIn(0F, 1F), g.coerceIn(0F, 1F), b.coerceIn(0F, 1F), a.coerceIn(0F, 1F))
    }
}
