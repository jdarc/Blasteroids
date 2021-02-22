package engine.math

import org.khronos.webgl.Float32Array
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("unused")
object Scalar {
    const val PI = 3.14159265359F
    const val TAU = 6.28318530718F
    const val EPSILON = 0.00000001F

    fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && abs(a - b) <= epsilon

    fun hypot(x: Float, y: Float, z: Float) = sqrt(x.sqr() + y.sqr() + z.sqr())

    fun isPot(value: Int) = (value > 0) && (value and value - 1) == 0

    fun toRadians(degrees: Float) = degrees * PI / 180F

    fun toDegrees(radians: Float) = radians * 180F / PI

    fun Float.sqr() = this.pow(2)

    fun FloatArray.pack() = Float32Array(this.toTypedArray())
}
