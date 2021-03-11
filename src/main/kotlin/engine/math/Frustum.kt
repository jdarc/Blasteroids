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

class Frustum(view: Matrix4, projection: Matrix4) {
    private val planes = (projection * view).run {
        arrayOf(
            Plane.create(m30 + m00, m31 + m01, m32 + m02, m33 + m03),
            Plane.create(m30 - m00, m31 - m01, m32 - m02, m33 - m03),
            Plane.create(m30 + m10, m31 + m11, m32 + m12, m33 + m13),
            Plane.create(m30 - m10, m31 - m11, m32 - m12, m33 - m13),
            Plane.create(m30 + m20, m31 + m21, m32 + m22, m33 + m23),
            Plane.create(m30 - m20, m31 - m21, m32 - m22, m33 - m23)
        )
    }

    fun intersect(ray: Ray) = ray.getPoint(planes.map { it.intersect(ray) }.minOf { it })

    fun contains(box: Aabb) = planes.any { box.pointsBehind(it) == 8 }.not()
}
