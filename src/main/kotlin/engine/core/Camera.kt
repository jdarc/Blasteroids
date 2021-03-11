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

package engine.core

import engine.math.Frustum
import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.math.asin
import kotlin.math.atan2

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Camera(fov: Float = Scalar.PI / 4F, aspectRatio: Float = 1F, nearPlane: Float = 1F, farPlane: Float = 1000F) {
    private val eyePos = Float32Array(arrayOf(0F, 0F, 1F))
    private val lookAt = Float32Array(arrayOf(0F, 0F, 0F))
    private val worldUp = Float32Array(arrayOf(0F, 1F, 0F))
    private var viewMatrix = Matrix4.IDENTITY
    private var projMatrix = Matrix4.IDENTITY
    private var camFrustum = Frustum(viewMatrix, projMatrix)
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

    val frustum get() = regenerateMatrix().run { camFrustum }

    val direction get() = Vector3.normalize(Vector3(lookAt[0] - eyePos[0], lookAt[1] - eyePos[1], lookAt[2] - eyePos[2]))

    val yaw get() = -atan2(-view.m20, view.m22)
    val pitch get() = -asin(view.m21)

    val view get() = regenerateMatrix().run { viewMatrix }
    val projection get() = regenerateMatrix().run { projMatrix }

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

    private fun regenerateMatrix() {
        if (dirty) {
            viewMatrix = Matrix4.createLookAt(position, target, up)
            projMatrix = Matrix4.createPerspectiveFov(fieldOfView, aspectRatio, nearDistance, farDistance)
            camFrustum = Frustum(viewMatrix, projMatrix)
            dirty = false
        }
    }
}
