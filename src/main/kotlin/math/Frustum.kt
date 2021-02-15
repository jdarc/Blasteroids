package math

class Frustum(val view: Matrix4, val projection: Matrix4) {
    private val mat = projection * view
    private val lft = Plane.create(mat.m30 + mat.m00, mat.m31 + mat.m01, mat.m32 + mat.m02, mat.m33 + mat.m03)
    private val rht = Plane.create(mat.m30 - mat.m00, mat.m31 - mat.m01, mat.m32 - mat.m02, mat.m33 - mat.m03)
    private val bot = Plane.create(mat.m30 + mat.m10, mat.m31 + mat.m11, mat.m32 + mat.m12, mat.m33 + mat.m13)
    private val top = Plane.create(mat.m30 - mat.m10, mat.m31 - mat.m11, mat.m32 - mat.m12, mat.m33 - mat.m13)
    private val ner = Plane.create(mat.m30 + mat.m20, mat.m31 + mat.m21, mat.m32 + mat.m22, mat.m33 + mat.m23)
    private val far = Plane.create(mat.m30 - mat.m20, mat.m31 - mat.m21, mat.m32 - mat.m22, mat.m33 - mat.m23)

    fun intersects(box: Aabb): Boolean {
        if (box.pointsBehind(ner) == 8) return false
        if (box.pointsBehind(far) == 8) return false
        if (box.pointsBehind(lft) == 8) return false
        if (box.pointsBehind(rht) == 8) return false
        if (box.pointsBehind(top) == 8) return false
        if (box.pointsBehind(bot) == 8) return false
        return true
    }
}

