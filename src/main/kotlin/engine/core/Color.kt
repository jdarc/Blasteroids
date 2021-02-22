package engine.core

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class Color(val argb: Int) {

    val alpha = unpack(argb, 24)
    val red = unpack(argb, 16)
    val green = unpack(argb, 8)
    val blue = unpack(argb)

    companion object {
        val BLACK = create(0x000000)
        val WHITE = create(0xFFFFFF)
        val RED = create(0xFF0000)
        val GREEN = create(0x00FF00)
        val BLUE = create(0x0000FF)

        fun create(rgb: Int, alpha: Int = 0xFF) = create(unpack(rgb, 16), unpack(rgb, 8), unpack(rgb), unpack(alpha))

        fun create(r: Float, g: Float, b: Float, a: Float = 1F) = Color(toArgb(a, r, g, b))

        private fun pack(v: Float, bits: Int = 0) = (v * 255F).toInt().coerceIn(0, 255).shl(bits)

        private fun unpack(argb: Int, bits: Int = 0) = (argb shr bits and 0xFF) / 255F

        private fun toArgb(a: Float = 1F, r: Float, g: Float, b: Float) = pack(a, 24) or pack(r, 16) or pack(g, 8) or pack(b)
    }
}
