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

import engine.math.Scalar.sqr
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")
class Aabb(min: Vector3 = Vector3.POSITIVE_INFINITY, max: Vector3 = Vector3.NEGATIVE_INFINITY) {
    private val min = floatArrayOf(min.x, min.y, min.z)
    private val max = floatArrayOf(max.x, max.y, max.z)

    val width get() = max[0] - min[0]
    val height get() = max[1] - min[1]
    val depth get() = max[2] - min[2]

    val center get() = Vector3((min[0] + max[0]) / 2F, (min[1] + max[1]) / 2F, (min[2] + max[2]) / 2F)
    val radius get() = sqrt((max[0] - min[0]).sqr() + (max[1] - min[1]).sqr() + (max[2] - min[2]).sqr()) / 2F

    fun reset(): Aabb {
        min.fill(Float.POSITIVE_INFINITY)
        max.fill(Float.NEGATIVE_INFINITY)
        return this
    }

    fun contains(v: Vector3) =
        (v.x >= min[0]) && (v.y >= min[1]) && (v.z >= min[2]) &&
        (v.x <= max[0]) && (v.y <= max[1]) && (v.z <= max[2])

    fun pointsBehind(plane: Plane) =
        (if (plane.dot(min[0], max[1], min[2]) < 0) 1 else 0) +
        (if (plane.dot(max[0], max[1], min[2]) < 0) 1 else 0) +
        (if (plane.dot(max[0], min[1], min[2]) < 0) 1 else 0) +
        (if (plane.dot(min[0], min[1], min[2]) < 0) 1 else 0) +
        (if (plane.dot(min[0], max[1], max[2]) < 0) 1 else 0) +
        (if (plane.dot(max[0], max[1], max[2]) < 0) 1 else 0) +
        (if (plane.dot(max[0], min[1], max[2]) < 0) 1 else 0) +
        (if (plane.dot(min[0], min[1], max[2]) < 0) 1 else 0)

    fun aggregate(x: Float, y: Float, z: Float): Aabb {
        if (x.isFinite() && y.isFinite() && z.isFinite()) {
            min[0] = min(x, min[0])
            min[1] = min(y, min[1])
            min[2] = min(z, min[2])
            max[0] = max(x, max[0])
            max[1] = max(y, max[1])
            max[2] = max(z, max[2])
        }
        return this
    }

    fun aggregate(point: Vector3) = aggregate(point.x, point.y, point.z)

    fun aggregate(other: Aabb) =
        aggregate(other.min[0], other.min[1], other.min[2]).aggregate(other.max[0], other.max[1], other.max[2])

    fun aggregate(box: Aabb, transform: Matrix4): Aabb {
        val a = transform.m00 * box.min[0]
        val b = transform.m10 * box.min[0]
        val c = transform.m20 * box.min[0]
        val d = transform.m01 * box.min[1]
        val e = transform.m11 * box.min[1]
        val f = transform.m21 * box.min[1]
        val g = transform.m02 * box.min[2]
        val h = transform.m12 * box.min[2]
        val i = transform.m22 * box.min[2]
        val j = transform.m00 * box.max[0]
        val k = transform.m10 * box.max[0]
        val l = transform.m20 * box.max[0]
        val m = transform.m01 * box.max[1]
        val n = transform.m11 * box.max[1]
        val o = transform.m21 * box.max[1]
        val p = transform.m02 * box.max[2]
        val q = transform.m12 * box.max[2]
        val r = transform.m22 * box.max[2]
        aggregate(a + m + g + transform.m03, b + n + h + transform.m13, c + o + i + transform.m23)
        aggregate(j + m + g + transform.m03, k + n + h + transform.m13, l + o + i + transform.m23)
        aggregate(j + d + g + transform.m03, k + e + h + transform.m13, l + f + i + transform.m23)
        aggregate(a + d + g + transform.m03, b + e + h + transform.m13, c + f + i + transform.m23)
        aggregate(a + m + p + transform.m03, b + n + q + transform.m13, c + o + r + transform.m23)
        aggregate(j + m + p + transform.m03, k + n + q + transform.m13, l + o + r + transform.m23)
        aggregate(j + d + p + transform.m03, k + e + q + transform.m13, l + f + r + transform.m23)
        aggregate(a + d + p + transform.m03, b + e + q + transform.m13, c + f + r + transform.m23)
        return this
    }
}
