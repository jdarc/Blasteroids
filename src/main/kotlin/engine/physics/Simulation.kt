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
import engine.tools.Publisher
import engine.tools.Subscriber

class Simulation : Publisher<String, CollisionInfo> {
    private val listeners = mutableMapOf<String, MutableSet<Subscriber<CollisionInfo>>>()
    private val bodies = mutableListOf<RigidBody>()

    override fun subscribe(eventType: String, subscriber: Subscriber<CollisionInfo>) {
        listeners.getOrPut(eventType, { mutableSetOf() }).add(subscriber)
    }

    override fun unsubscribe(eventType: String, subscriber: Subscriber<CollisionInfo>) {
        listeners[eventType]?.remove(subscriber)
        console.log(listeners[eventType]?.size)
    }

    override fun notify(eventType: String, data: CollisionInfo) {
        listeners[eventType]?.forEach { it.update(data) }
    }

    fun addBody(body: RigidBody) {
        if (!bodies.contains(body)) bodies.add(body)
    }

    fun removeBody(body: RigidBody) {
        if (bodies.contains(body)) bodies.remove(body)
    }

    fun update(timeStep: Float) {
        bodies.forEach {
            it.integrate(timeStep)
            it.clearForces()
        }
        detectCollisions()
    }

    private fun detectCollisions() {
        for (body0 in bodies) {
            for (body1 in bodies) {
                if (body0 != body1 && body0.hitTest(body1)) {
                    val dirToBody0 = Vector3.normalize(body0.position - body1.position)
                    val collisionPoint = body1.position + dirToBody0 * body1.boundingSphere
                    notify("Collision", CollisionInfo(body0, body1, dirToBody0, collisionPoint))
                }
            }
        }
    }
}
