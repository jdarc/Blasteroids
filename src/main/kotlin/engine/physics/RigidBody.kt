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

import engine.math.Matrix4
import engine.math.Vector3

class RigidBody {
    private val particle = Particle()

    var data: Any = ""

    var boundingSphere = 1F
        set(value) {
            field = value.coerceAtLeast(0.00001F)
        }

    var dampingFactor
        get() = particle.dampingFactor
        set(value) {
            particle.dampingFactor = value
        }

    var position
        get() = particle.position
        set(value) {
            particle.position = value
        }

    var velocity: Vector3
        get() = particle.velocity
        set(value) {
            particle.velocity = value
        }

    var orientation = Matrix4.IDENTITY

    fun addForce(force: Vector3) = particle.addForce(force)

    fun clearForces() = particle.clearForces()

    fun integrate(timeStep: Float) = particle.integrate(timeStep)

    fun hitTest(other: RigidBody) = (position - other.position).length <= boundingSphere + other.boundingSphere
}
