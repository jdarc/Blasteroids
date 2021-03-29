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

@Suppress("unused")
class Plane private constructor(val normal: Vector3, val distance: Float) {

    fun dot(rhs: Vector3) = dot(rhs.x, rhs.y, rhs.z)

    fun dot(x: Float, y: Float, z: Float) = (normal.x * x) + (normal.y * y) + (normal.z * z) + distance

    fun intersect(ray: Ray): Float {
        val vd = Vector3.dot(normal, ray.direction)
        if (vd != 0F) {
            val t = -(Vector3.dot(normal, ray.origin) - distance) / vd
            if (t > 0F) return t
        }
        return Float.POSITIVE_INFINITY
    }

    companion object {
        fun normalize(plane: Plane): Plane {
            val length = plane.normal.length()
            if (length < Scalar.EPSILON) throw ArithmeticException(length.toString())
            if (Scalar.equals(length, 1F)) return plane
            return Plane(plane.normal / length, plane.distance / length)
        }

        fun create(vector: Vector3, distance: Float) = create(vector.x, vector.y, vector.z, distance)
        fun create(x: Float, y: Float, z: Float, d: Float): Plane {
            val length = Scalar.hypot(x, y, z)
            if (length < Scalar.EPSILON) throw ArithmeticException(length.toString())
            return Plane(Vector3(x / length, y / length, z / length), d / length)
        }

        fun createFromPoints(v0: Vector3, v1: Vector3, v2: Vector3): Plane {
            val ax = v1.x - v0.x
            val ay = v1.y - v0.y
            val az = v1.z - v0.z
            val bx = v2.x - v0.x
            val by = v2.y - v0.y
            val bz = v2.z - v0.z
            val nx = (ay * bz) - (az * by)
            val ny = (az * bx) - (ax * bz)
            val nz = (ax * by) - (ay * bx)
            val length = Scalar.hypot(nx, ny, nz)
            if (length < Scalar.EPSILON) throw ArithmeticException(length.toString())
            val invLen = 1F / length
            val normal = Vector3(nx * invLen, ny * invLen, nz * invLen)
            val distance = -normal.x * v0.x - normal.y * v0.y - normal.z * v0.z
            return Plane(normal, distance)
        }
    }
}
