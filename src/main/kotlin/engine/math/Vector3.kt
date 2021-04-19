/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package engine.math

import engine.math.Scalar.abs
import engine.math.Scalar.max
import engine.math.Scalar.min
import engine.math.Scalar.sqr
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.math.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")
data class Vector3(val x: Float, val y: Float, val z: Float) {

    constructor(s: Number) : this(s.toFloat(), s.toFloat(), s.toFloat())

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

    constructor(src: Float32Array, offset: Int = 0) : this(src[offset + 0], src[offset + 1], src[offset + 2])

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    operator fun div(rhs: Float) = Vector3(x / rhs, y / rhs, z / rhs)

    operator fun div(rhs: Vector3) = Vector3(x / rhs.x, y / rhs.y, z / rhs.z)

    operator fun times(rhs: Float) = Vector3(x * rhs, y * rhs, z * rhs)

    operator fun times(rhs: Vector3) = Vector3(x * rhs.x, y * rhs.y, z * rhs.z)

    operator fun times(m: Matrix4) = Vector3(
        x * m.m00 + y * m.m10 + z * m.m20 + m.m30,
        x * m.m01 + y * m.m11 + z * m.m21 + m.m31,
        x * m.m02 + y * m.m12 + z * m.m22 + m.m32
    )

    fun length() = sqrt(lengthSquared())

    fun lengthSquared() = dot(this, this)

    fun toArray(dst: Float32Array = Float32Array(3), offset: Int = 0) = dst.apply {
        this[offset + 0] = x
        this[offset + 1] = y
        this[offset + 2] = z
    }

    companion object {
        private const val EPSILON = 0.00001F

        val ONE = Vector3(1F, 1F, 1F)
        val ZERO = Vector3(0F, 0F, 0F)

        val UNIT_X = Vector3(1F, 0F, 0F)
        val UNIT_Y = Vector3(0F, 1F, 0F)
        val UNIT_Z = Vector3(0F, 0F, 1F)

        val POSITIVE_INFINITY = Vector3(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector3(Float.NEGATIVE_INFINITY)

        fun isZero(v: Vector3) = abs(v.x) < EPSILON && abs(v.y) < EPSILON && abs(v.z) < EPSILON

        fun abs(value: Vector3) = Vector3(abs(value.x), abs(value.y), abs(value.z))

        fun maxComponent(v: Vector3) = max(v.x, max(v.y, v.z))

        fun minComponent(v: Vector3) = min(v.x, min(v.y, v.z))

        fun min(a: Vector3, b: Vector3) = Vector3(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))

        fun max(a: Vector3, b: Vector3) = Vector3(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

        fun clamp(value: Vector3, min: Vector3, max: Vector3) = min(max, max(value, min))

        fun clamp(v: Vector3, min: Float, max: Float) = Vector3(v.x.coerceIn(min, max), v.y.coerceIn(min, max), v.z.coerceIn(min, max))

        fun dot(lhs: Vector3, rhs: Vector3) = (lhs.x * rhs.x) + (lhs.y * rhs.y) + (lhs.z * rhs.z)

        fun cross(a: Vector3, b: Vector3) = Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)

        fun normalize(v: Vector3) = v * Scalar.invSqrt(dot(v, v))

        fun crossDot(xa: Vector3, xb: Vector3, dv: Vector3): Float {
            val cx = xa.y * xb.z - xa.z * xb.y
            val cy = xa.z * xb.x - xa.x * xb.z
            val cz = xa.x * xb.y - xa.y * xb.x
            return cx * dv.x + cy * dv.y + cz * dv.z
        }

        fun crossLength(a: Vector3, b: Vector3) =
            sqrt((a.y * b.z - a.z * b.y).sqr() + (a.z * b.x - a.x * b.z).sqr() + (a.x * b.y - a.y * b.x).sqr())

        fun equals(lhs: Vector3, rhs: Vector3, epsilon: Float = Scalar.EPSILON) =
            Scalar.equals(lhs.x, rhs.x, epsilon) &&
            Scalar.equals(lhs.y, rhs.y, epsilon) &&
            Scalar.equals(lhs.z, rhs.z, epsilon)

        fun random(x: Float = 1F, y: Float = 1F, z: Float = 1F): Vector3 {
            val rx = 2F * x * Scalar.rnd() - x
            val ry = 2F * y * Scalar.rnd() - y
            val rz = 2F * z * Scalar.rnd() - z
            return normalize(Vector3(rx, ry, rz))
        }
    }
}
