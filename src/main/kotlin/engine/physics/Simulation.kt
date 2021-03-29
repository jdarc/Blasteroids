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

import engine.math.Scalar
import engine.math.Scalar.max
import engine.math.Scalar.min
import engine.math.Vector3
import engine.physics.collision.CollisionHandler
import engine.physics.collision.CollisionSystem
import game.EventBus

class Simulation(private val events: EventBus, collisionFilter: (RigidBody, RigidBody) -> Boolean) : CollisionHandler {
    private val collisionSystem = CollisionSystem(collisionFilter)
    private val collisions = mutableListOf<Collision>()
    private val bodies = mutableSetOf<RigidBody>()

    val constraints = mutableSetOf<Constraint>()

    fun addBody(body: RigidBody) {
        bodies.add(body)
    }

    fun removeBody(body: RigidBody) {
        bodies.remove(body)
    }

    override fun impact(body0: RigidBody, body1: RigidBody, normal: Vector3, r0: Vector3, r1: Vector3, penetration: Float) {
        collisions.add(Collision(body0, body1, normal, r0, r1, penetration))
        events.notify(COLLISION_EVENT, CollisionEvent(body0, body1))
    }

    fun integrate(dt: Float) {
        val bodies = bodies.toTypedArray()

        bodies.forEach {
            it.storeState()
            it.updateVelocity(dt)
            it.updatePosition(dt)
        }

        collisions.clear()
        collisionSystem.detect(bodies, this)

        bodies.forEach { it.restoreState() }

        collisions.forEach { processCollision(it, dt) }

        for (body in bodies) body.updateVelocity(dt)

        constraints.forEach { it.apply(dt) }

        bodies.forEach {
            it.updatePosition(dt)
            it.clearForces()
        }
    }

    private fun processCollision(collision: Collision, dt: Float) {
        val tolerance = 1F / (Scalar.TINY + ALLOWED_PENETRATION)
        val body0 = collision.body0
        val body1 = collision.body1
        val normal = collision.normal
        val r0 = collision.r0
        val r1 = collision.r1
        val penetration = collision.penetration

        var denominator = body0.inverseMass + Vector3.dot(normal, Vector3.cross(body0.worldInvInertia * Vector3.cross(r0, normal), r0)) +
                          body1.inverseMass + Vector3.dot(normal, Vector3.cross(body1.worldInvInertia * Vector3.cross(r1, normal), r1))

        val diffPenetration = penetration - ALLOWED_PENETRATION
        var minSeparationVel = if (penetration > ALLOWED_PENETRATION) diffPenetration / dt
        else (-0.1F * diffPenetration * tolerance).coerceIn(Scalar.TINY, 1F) * diffPenetration / max(dt, Scalar.TINY)

        denominator = max(denominator, Scalar.TINY)
        minSeparationVel = min(minSeparationVel, MAX_VEL_MAG)

        val normalVel = Vector3.dot(body0.velocityRelativeTo(r0) - body1.velocityRelativeTo(r1), normal)
        if (normalVel > minSeparationVel) return

        var finalNormalVel = -collision.restitution * normalVel
        if (finalNormalVel < MIN_VEL_FOR_PROCESSING) finalNormalVel = minSeparationVel

        val deltaVel = finalNormalVel - normalVel
        if (deltaVel <= MIN_VEL_FOR_PROCESSING) return

        if (denominator < Scalar.TINY) denominator = Scalar.TINY
        val normalImpulse = deltaVel / denominator

        body0.applyBodyWorldImpulse(normal * normalImpulse, r0)
        body1.applyBodyWorldImpulse(-normal * normalImpulse, r1)

        val vrNew = body0.velocityRelativeTo(r0) - body1.velocityRelativeTo(r1)
        var tangentVel = normal * Vector3.dot(vrNew, normal) - vrNew
        val tangentSpeed = tangentVel.length()
        if (tangentSpeed < MIN_VEL_FOR_PROCESSING) return
        tangentVel /= tangentSpeed

        denominator = body0.inverseMass + Vector3.dot(tangentVel, Vector3.cross(body0.worldInvInertia * Vector3.cross(r0, tangentVel), r0)) +
                      body1.inverseMass + Vector3.dot(tangentVel, Vector3.cross(body1.worldInvInertia * Vector3.cross(r1, tangentVel), r1))
        if (denominator < Scalar.TINY) return

        val impulseToReserve = tangentSpeed / denominator
        val i = if (impulseToReserve < collision.friction * normalImpulse) impulseToReserve else collision.friction * normalImpulse
        body0.applyBodyWorldImpulse(tangentVel * i, r0)
        body1.applyBodyWorldImpulse(-tangentVel * i, r1)
    }

    companion object {
        const val COLLISION_EVENT = "collision"

        private const val ALLOWED_PENETRATION = 0.01F
        private const val MAX_VEL_MAG = 0.5F
        private const val MIN_VEL_FOR_PROCESSING = 0.0001F

        private class Collision(val body0: RigidBody, val body1: RigidBody, val normal: Vector3, val r0: Vector3, val r1: Vector3, val penetration: Float) {
            val friction = (body0.skin.friction + body1.skin.friction) * 0.5F
            var restitution = (body0.skin.restitution + body1.skin.restitution) * 0.5F
        }
    }
}
