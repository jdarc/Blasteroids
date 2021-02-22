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
