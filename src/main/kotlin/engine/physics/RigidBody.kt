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
import engine.math.Scalar
import engine.math.Scalar.sqr
import engine.math.Vector3
import engine.physics.geometry.CollisionSkin

class RigidBody(val skin: CollisionSkin) {
    val id = ID++

    private var bodyInertia = Matrix4.IDENTITY
    private var bodyInvInertia = Matrix4.IDENTITY
    private var invOrientation = Matrix4.IDENTITY

    private val tmpState = PhysicsState()
    private val newState = PhysicsState()

    var data: Any = ""

    var mass = 1F
        set(value) {
            field = value.coerceAtLeast(Scalar.TINY)
            bodyInertia = skin.calculateBodyInertia(field)
            bodyInvInertia = Matrix4.invert(bodyInertia)
            updateInertia()
        }

    val inverseMass get() = 1F / mass

    var position
        get() = newState.position
        set(value) {
            newState.position = value
        }

    var orientation
        get() = newState.orientation
        set(value) {
            newState.orientation = value
            updateInertia()
        }

    var linearVelocity
        get() = newState.linearVelocity
        set(value) {
            newState.linearVelocity = value
        }

    var angularVelocity
        get() = newState.angularVelocity
        set(value) {
            newState.angularVelocity = value
        }

    private var force = Vector3.ZERO

    private var torque = Vector3.ZERO

    private var worldInertia = Matrix4.IDENTITY

    var worldInvInertia = Matrix4.IDENTITY
        private set

    fun storeState() = tmpState.copy(newState)

    fun restoreState() {
        newState.copy(tmpState)
        updateInertia()
    }

    fun hitTest(other: RigidBody) = (position - other.position).lengthSquared() <= (skin.boundingSphere + other.skin.boundingSphere).sqr()

    fun clearForces() {
        force = Vector3.ZERO
        torque = Vector3.ZERO
    }

    fun addWorldForce(force: Vector3) {
        this.force += force
    }

    fun applyBodyWorldImpulse(impulse: Vector3, delta: Vector3) {
        linearVelocity += impulse * inverseMass
        angularVelocity += worldInvInertia * Vector3.cross(delta, impulse)
    }

    fun updateVelocity(dt: Float) {
        linearVelocity += force * (inverseMass * dt) * 0.995F
        angularVelocity += worldInvInertia * torque * dt * 0.995F
    }

    fun updatePosition(dt: Float) {
        val angMomBefore = Matrix4.transformNormal(worldInertia, angularVelocity)

        position += linearVelocity * dt
        val ang = angularVelocity.length() * dt
        if (ang > Scalar.TINY) orientation = Matrix4.createFromAxisAngle(angularVelocity, ang) * orientation

        updateInertia()
        angularVelocity = Matrix4.transformNormal(worldInvInertia, angMomBefore)
        skin.origin = position
        skin.basis = orientation
    }

    fun velocityRelativeTo(position: Vector3) = linearVelocity + Vector3.cross(angularVelocity, position)

    private fun updateInertia() {
        invOrientation = Matrix4.transpose(orientation)
        worldInertia = orientation * bodyInertia * invOrientation
        worldInvInertia = orientation * bodyInvInertia * invOrientation
    }

    override fun hashCode() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false
        other as RigidBody
        return id == other.id
    }

    init {
        mass = 1F
    }

    private companion object {
        var ID = 0

        class PhysicsState {
            var position = Vector3.ZERO
            var orientation = Matrix4.IDENTITY
            var linearVelocity = Vector3.ZERO
            var angularVelocity = Vector3.ZERO

            fun copy(other: PhysicsState) {
                position = other.position
                orientation = other.orientation
                linearVelocity = other.linearVelocity
                angularVelocity = other.angularVelocity
            }
        }
    }
}
