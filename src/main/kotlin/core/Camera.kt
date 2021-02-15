@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package core

import math.Matrix4
import math.Scalar
import math.Vector3
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set

class Camera(fov: Float = Scalar.PI / 4F, aspectRatio: Float = 1F, nearPlane: Float = 1F, farPlane: Float = 1000F) {
    private val eyePos = Float32Array(arrayOf(0F, 0F, 1F))
    private val lookAt = Float32Array(arrayOf(0F, 0F, 0F))
    private val worldUp = Float32Array(arrayOf(0F, 1F, 0F))
    private var viewMatrix = Matrix4.IDENTITY
    private var projMatrix = Matrix4.IDENTITY
    private var dirty = true

    var fieldOfView = fov
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Scalar.PI)
            dirty = true
        }

    var aspectRatio = aspectRatio
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            dirty = true
        }

    var nearDistance = nearPlane
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            if (nearDistance >= farDistance) throw RuntimeException("near distance must be less than far distance")
            dirty = true
        }

    var farDistance = farPlane
        set(value) {
            field = value.coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            if (farDistance <= nearDistance) throw RuntimeException("far distance must be greater than near distance")
            dirty = true
        }

    var position
        get() = Vector3(eyePos)
        set(value) {
            value.toArray(eyePos)
            dirty = true
        }

    var target
        get() = Vector3(lookAt)
        set(value) {
            value.toArray(lookAt)
            dirty = true
        }

    var up
        get() = Vector3(worldUp)
        set(value) {
            Vector3.normalize(value).toArray(worldUp)
            dirty = true
        }

    val direction get() = Vector3.normalize(Vector3(lookAt[0] - eyePos[0], lookAt[1] - eyePos[1], lookAt[2] - eyePos[2]))

    val view get() = regenerateMatrix(viewMatrix)
    val projection get() = regenerateMatrix(projMatrix)

    fun moveUp(amount: Float) = moveDown(-amount)
    fun moveDown(amount: Float) = strafe(view.m10 * amount, view.m11 * amount, view.m12 * amount)

    fun moveBackward(amount: Float) = moveForward(-amount)
    fun moveForward(amount: Float) = strafe(view.m20 * amount, view.m21 * amount, view.m22 * amount)

    fun moveRight(amount: Float) = moveLeft(-amount)
    fun moveLeft(amount: Float) = strafe(view.m00 * amount, view.m01 * amount, view.m02 * amount)

    fun orient(dir: Vector3) {
        lookAt[0] = eyePos[0] + dir.x
        lookAt[1] = eyePos[1] + dir.y
        lookAt[2] = eyePos[2] + dir.z
        dirty = true
    }

    private fun strafe(dx: Float, dy: Float, dz: Float) {
        eyePos[0] -= dx
        eyePos[1] -= dy
        eyePos[2] -= dz
        lookAt[0] -= dx
        lookAt[1] -= dy
        lookAt[2] -= dz
        dirty = true
    }

    private fun regenerateMatrix(pass: Matrix4): Matrix4 {
        if (dirty) {
            viewMatrix = Matrix4.createLookAt(position, target, up)
            projMatrix = Matrix4.createPerspectiveFov(fieldOfView, aspectRatio, nearDistance, farDistance)
            dirty = false
        }
        return pass
    }
}
