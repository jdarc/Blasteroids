package engine.physics

import engine.math.Vector3
import kotlin.math.max

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Particle {
    private var pos = floatArrayOf(0F, 0F, 0F)
    private var vel = floatArrayOf(0F, 0F, 0F)
    private var acc = floatArrayOf(0F, 0F, 0F)

    var position
        get() = Vector3(pos[0], pos[1], pos[2])
        set(value) {
            pos[0] = value.x
            pos[1] = value.y
            pos[2] = value.z
        }

    var velocity
        get() = Vector3(vel[0], vel[1], vel[2])
        set(value) {
            vel[0] = value.x
            vel[1] = value.y
            vel[2] = value.z
        }

    var acceleration
        get() = Vector3(acc[0], acc[1], acc[2])
        set(value) {
            acc[0] = value.x
            acc[1] = value.y
            acc[2] = value.z
        }

    var dampingFactor = 0F
        set(value) {
            field = max(0F, value)
        }

    fun addForce(force: Vector3) = add(acc, force, 1F)

    fun clearForces() = mul(acc, 0F)

    fun integrate(timeStep: Float) {
        mul(vel, (1F - dampingFactor * timeStep).coerceIn(0F, 1F))
        add(acc, vel, timeStep)
        add(vel, pos, timeStep)
    }

    private fun mul(dst: FloatArray, scale: Float) {
        dst[0] *= scale
        dst[1] *= scale
        dst[2] *= scale
    }

    private fun add(src: FloatArray, dst: FloatArray, delta: Float) {
        dst[0] += src[0] * delta
        dst[1] += src[1] * delta
        dst[2] += src[2] * delta
    }

    private fun add(dst: FloatArray, v: Vector3, delta: Float) {
        dst[0] += v.x * delta
        dst[1] += v.y * delta
        dst[2] += v.z * delta
    }
}
