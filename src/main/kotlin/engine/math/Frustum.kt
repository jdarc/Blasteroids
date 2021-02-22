package engine.math

import engine.core.Camera

class Frustum(camera: Camera) {
    private val planes = (camera.projection * camera.view).run {
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
