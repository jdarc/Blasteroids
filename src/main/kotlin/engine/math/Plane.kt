package engine.math

@Suppress("unused")
class Plane private constructor(val normal: Vector3, val distance: Float) {

    fun dot(rhs: Vector3) = dot(rhs.x, rhs.y, rhs.z)
    fun dot(x: Float, y: Float, z: Float) = (normal.x * x) + (normal.y * y) + (normal.z * z) + distance

    companion object {
        fun normalize(plane: Plane): Plane {
            val length = plane.normal.length
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
