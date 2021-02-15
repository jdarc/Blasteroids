@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package math

import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.math.sqrt

data class Vector3(val x: Float, val y: Float, val z: Float) {

    constructor(s: Float) : this(s, s, s)

    constructor(src: Float32Array, offset: Int = 0) : this(src[offset + 0], src[offset + 1], src[offset + 2])

    inline val length get() = sqrt(lengthSquared)

    inline val lengthSquared get() = dot(this, this)

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    operator fun times(rhs: Float) = Vector3(x * rhs, y * rhs, z * rhs)

    operator fun div(rhs: Float) = Vector3(x / rhs, y / rhs, z / rhs)

    fun toArray(dst: Float32Array = Float32Array(3), offset: Int = 0) = dst.apply {
        this[offset + 0] = x
        this[offset + 1] = y
        this[offset + 2] = z
    }

    companion object {
        val ONE = Vector3(1F, 1F, 1F)
        val ZERO = Vector3(0F, 0F, 0F)

        val UNIT_X = Vector3(1F, 0F, 0F)
        val UNIT_Y = Vector3(0F, 1F, 0F)
        val UNIT_Z = Vector3(0F, 0F, 1F)

        val POSITIVE_INFINITY = Vector3(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector3(Float.NEGATIVE_INFINITY)

        fun dot(lhs: Vector3, rhs: Vector3) = (lhs.x * rhs.x) + (lhs.y * rhs.y) + (lhs.z * rhs.z)

        fun normalize(v: Vector3) = v / v.length

        fun cross(lhs: Vector3, rhs: Vector3) = Vector3(
            (lhs.y * rhs.z) - (lhs.z * rhs.y),
            (lhs.z * rhs.x) - (lhs.x * rhs.z),
            (lhs.x * rhs.y) - (lhs.y * rhs.x)
        )

        fun equals(lhs: Vector3, rhs: Vector3, epsilon: Float = Scalar.EPSILON) =
            Scalar.equals(lhs.x, rhs.x, epsilon) &&
            Scalar.equals(lhs.y, rhs.y, epsilon) &&
            Scalar.equals(lhs.z, rhs.z, epsilon)

        fun clamp(v: Vector3, min: Float = 0F, max: Float = 1F) = Vector3(
            v.x.coerceIn(min, max),
            v.y.coerceIn(min, max),
            v.z.coerceIn(min, max)
        )
    }
}
