@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")

package math

import math.Scalar.sqr
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Aabb(private var min: Vector3 = Vector3.POSITIVE_INFINITY, private var max: Vector3 = Vector3.NEGATIVE_INFINITY) {
    val minimum get() = min
    val maximum get() = max

    val width get() = max.x - min.x
    val height get() = max.y - min.y
    val depth get() = max.z - min.z

    val center get() = Vector3((min.x + max.x) / 2F, (min.y + max.y) / 2F, (min.z + max.z) / 2F)
    val radius get() = sqrt((max.x - min.x).sqr() + (max.y - min.y).sqr() + (max.z - min.z).sqr()) / 2F

    fun reset(): Aabb {
        min = Vector3.POSITIVE_INFINITY
        max = Vector3.NEGATIVE_INFINITY
        return this
    }

    fun contains(v: Vector3) =
        (v.x >= min.x) && (v.y >= min.y) && (v.z >= min.z) &&
        (v.x <= max.x) && (v.y <= max.y) && (v.z <= max.z)

    fun pointsBehind(plane: Plane) =
        (if (plane.dot(min.x, max.y, min.z) < 0F) 1 else 0) +
        (if (plane.dot(max.x, max.y, min.z) < 0F) 1 else 0) +
        (if (plane.dot(max.x, min.y, min.z) < 0F) 1 else 0) +
        (if (plane.dot(min.x, min.y, min.z) < 0F) 1 else 0) +
        (if (plane.dot(min.x, max.y, max.z) < 0F) 1 else 0) +
        (if (plane.dot(max.x, max.y, max.z) < 0F) 1 else 0) +
        (if (plane.dot(max.x, min.y, max.z) < 0F) 1 else 0) +
        (if (plane.dot(min.x, min.y, max.z) < 0F) 1 else 0)

    fun aggregate(x: Float, y: Float, z: Float): Aabb {
        min = Vector3(min(min.x, x), min(min.y, y), min(min.z, z))
        max = Vector3(max(max.x, x), max(max.y, y), max(max.z, z))
        return this
    }

    fun aggregate(point: Vector3) = aggregate(point.x, point.y, point.z)

    fun aggregate(box: Aabb) = aggregate(box.minimum).aggregate(box.maximum)

    fun aggregate(box: Aabb, transform: Matrix4) {
        val a = transform.m00 * box.minimum.x
        val b = transform.m10 * box.minimum.x
        val c = transform.m20 * box.minimum.x
        val d = transform.m01 * box.minimum.y
        val e = transform.m11 * box.minimum.y
        val f = transform.m21 * box.minimum.y
        val g = transform.m02 * box.minimum.z
        val h = transform.m12 * box.minimum.z
        val i = transform.m22 * box.minimum.z
        val j = transform.m00 * box.maximum.x
        val k = transform.m10 * box.maximum.x
        val l = transform.m20 * box.maximum.x
        val m = transform.m01 * box.maximum.y
        val n = transform.m11 * box.maximum.y
        val o = transform.m21 * box.maximum.y
        val p = transform.m02 * box.maximum.z
        val q = transform.m12 * box.maximum.z
        val r = transform.m22 * box.maximum.z
        aggregate(a + m + g + transform.m03, b + n + h + transform.m13, c + o + i + transform.m23)
        aggregate(j + m + g + transform.m03, k + n + h + transform.m13, l + o + i + transform.m23)
        aggregate(j + d + g + transform.m03, k + e + h + transform.m13, l + f + i + transform.m23)
        aggregate(a + d + g + transform.m03, b + e + h + transform.m13, c + f + i + transform.m23)
        aggregate(a + m + p + transform.m03, b + n + q + transform.m13, c + o + r + transform.m23)
        aggregate(j + m + p + transform.m03, k + n + q + transform.m13, l + o + r + transform.m23)
        aggregate(j + d + p + transform.m03, k + e + q + transform.m13, l + f + r + transform.m23)
        aggregate(a + d + p + transform.m03, b + e + q + transform.m13, c + f + r + transform.m23)
    }
}
