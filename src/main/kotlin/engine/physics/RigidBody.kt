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
import engine.math.Vector3
import engine.physics.geometry.Shape
import kotlin.math.cos
import kotlin.math.sin

class RigidBody(val skin: Shape) {
    val id = ID++

    private var bodyInertia = Matrix4.IDENTITY
    private var bodyInvInertia = Matrix4.IDENTITY
    private var invOrientation = Matrix4.IDENTITY

    private val state = PhysicsState()

    var data: Any = ""

    var mass = 0F
        set(value) {
            field = value.coerceAtLeast(Float.MIN_VALUE)
            inverseMass = 1F / value
            bodyInertia = skin.calculateBodyInertia(field)
            bodyInvInertia = Matrix4.invert(bodyInertia)
            updateInertia()
        }

    var inverseMass = 0F
        private set

    var position
        get() = state.position
        set(value) {
            state.position = value
        }

    var orientation
        get() = state.orientation
        set(value) {
            state.orientation = value
            updateInertia()
        }

    var linearVelocity
        get() = state.linearVelocity
        set(value) {
            state.linearVelocity = value
        }

    var angularVelocity
        get() = state.angularVelocity
        set(value) {
            state.angularVelocity = value
        }

    private var force = Vector3.ZERO

    private var torque = Vector3.ZERO

    private var worldInertia = Matrix4.IDENTITY

    var worldInvInertia = Matrix4.IDENTITY
        private set

    fun hitTest(other: RigidBody): Boolean {
        val sumRadius = skin.boundingSphere + other.skin.boundingSphere
        return (position - other.position).lengthSquared <= sumRadius * sumRadius
    }

    fun clearForces() {
        force = Vector3.ZERO
        torque = Vector3.ZERO
    }

    fun addForce(force: Vector3) {
        this.force += force
    }

    fun applyImpulse(impulse: Vector3, delta: Vector3) {
        linearVelocity += impulse * inverseMass
        angularVelocity += worldInvInertia * Vector3.cross(delta, impulse)
    }

    fun integrate(dt: Float) {
        linearVelocity += force * (dt * inverseMass)
        angularVelocity += worldInvInertia * torque * dt

        val angMomBefore = worldInertia * angularVelocity

        position += linearVelocity * dt
        orientation = addAngularVelocityToOrientation(angularVelocity, orientation, dt)

        invOrientation = Matrix4.transpose(orientation)
        worldInvInertia = bodyInvInertia * orientation * invOrientation
        worldInertia = bodyInertia * orientation * invOrientation
        angularVelocity = worldInvInertia * angMomBefore

        skin.origin = position
        skin.basis = orientation
    }

    fun velocityRelativeTo(position: Vector3) = linearVelocity + Vector3.cross(angularVelocity, position)

    private fun updateInertia() {
        invOrientation = Matrix4.transpose(orientation)
        worldInertia = bodyInertia * orientation * invOrientation
        worldInvInertia = bodyInvInertia * orientation * invOrientation
    }

    private fun addAngularVelocityToOrientation(angularVelocity: Vector3, orientation: Matrix4, dt: Float): Matrix4 {
        val ang = angularVelocity.length
        if (ang <= Scalar.TINY) return orientation
        val dir = angularVelocity / ang
        val cos = cos(-ang * dt)
        val sin = sin(-ang * dt)
        val t = 1F - cos
        val r0 = cos + dir.x * dir.x * t
        val r5 = cos + dir.y * dir.y * t
        val ra = cos + dir.z * dir.z * t
        val r4 = dir.x * dir.y * t + dir.z * sin
        val r1 = dir.x * dir.y * t - dir.z * sin
        val r8 = dir.x * dir.z * t - dir.y * sin
        val r2 = dir.x * dir.z * t + dir.y * sin
        val r9 = dir.y * dir.z * t + dir.x * sin
        val r6 = dir.y * dir.z * t - dir.x * sin
        val m11 = r0 * orientation.m00 + r4 * orientation.m10 + r8 * orientation.m20
        val m12 = r1 * orientation.m00 + r5 * orientation.m10 + r9 * orientation.m20
        val m13 = r2 * orientation.m00 + r6 * orientation.m10 + ra * orientation.m20
        val m21 = r0 * orientation.m01 + r4 * orientation.m11 + r8 * orientation.m21
        val m22 = r1 * orientation.m01 + r5 * orientation.m11 + r9 * orientation.m21
        val m23 = r2 * orientation.m01 + r6 * orientation.m11 + ra * orientation.m21
        val m31 = r0 * orientation.m02 + r4 * orientation.m12 + r8 * orientation.m22
        val m32 = r1 * orientation.m02 + r5 * orientation.m12 + r9 * orientation.m22
        val m33 = r2 * orientation.m02 + r6 * orientation.m12 + ra * orientation.m22
        return Matrix4(m11, m12, m13, 0F, m21, m22, m23, 0F, m31, m32, m33, 0F, 0F, 0F, 0F, 1F)
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
        }
    }
}
