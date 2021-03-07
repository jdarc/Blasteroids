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

package game

import engine.components.MaterialOverride
import engine.components.Physics
import engine.core.Color
import engine.core.Material
import engine.core.Primitives
import engine.graph.BranchNode
import engine.graph.LeafNode
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.CollisionInfo
import engine.physics.RigidBody
import engine.physics.Simulation
import engine.physics.Simulation.Companion.COLLISION_EVENT
import engine.tools.Subscriber

class MissileNode(private val eventBus: EventBus, private val simulation: Simulation) : BranchNode(), Subscriber<Any> {
    private val body = RigidBody()

    override fun update(seconds: Float): Boolean {
        transform = Matrix4.create(body.position, Matrix4.IDENTITY, Vector3.ONE)
        return super.update(seconds)
    }

    fun fire(origin: Vector3, direction: Vector3) {
        body.position = origin
        body.velocity = direction * speed
    }

    fun destroy() {
        eventBus.unsubscribe(COLLISION_EVENT, this)
        simulation.removeBody(body)
        parent?.removeNodes(this)
    }

    init {
        addNodes(LeafNode(MISSILE_GEOMETRY))
        addComponents(MaterialOverride(MISSILE_MATERIAL), Physics(body))
        body.data = ObjectTypes.MISSILE
        body.boundingSphere = bounds.radius
        simulation.addBody(body)
        eventBus.subscribe(COLLISION_EVENT, this)
    }

    override fun notify(context: Any) {
        if (context is CollisionInfo && context.involves(body) && context.hasData(ObjectTypes.ASTEROID)) destroy()
    }

    companion object {
        private var speed = 50F

        private val MISSILE_GEOMETRY = Primitives.createSphere(0.2F, 8, 8)

        private val MISSILE_MATERIAL = Material().apply {
            colors.diffuse = Color.RED
            colors.specular = Color.WHITE
        }
    }
}

