package engine.math

data class Ray(val origin: Vector3, val direction: Vector3) {
    fun getPoint(t: Float) = origin + direction * t
}
