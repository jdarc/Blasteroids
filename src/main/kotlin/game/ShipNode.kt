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

import engine.components.Physics
import engine.components.WrapAround
import engine.graph.BranchNode
import engine.graph.Geometry
import engine.graph.LeafNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3
import engine.physics.RigidBody
import engine.physics.Simulation
import engine.tools.Scheduler

class ShipNode(eventBus: EventBus, simulation: Simulation, scheduler: Scheduler, shipGeometry: Geometry) : BranchNode() {
    private var angularSpeed = Scalar.PI
    private var thrustSpeed = 20F
    private var radians = 0F
    private val body = RigidBody().apply { dampingFactor = 0.5F }

    override fun update(seconds: Float): Boolean {
        val keyboard = Keyboard.getState()
        when {
            keyboard.isKeyDown(Keys.Left) -> radians += angularSpeed * seconds
            keyboard.isKeyDown(Keys.Right) -> radians -= angularSpeed * seconds
        }
        body.orientation = Matrix4.createRotationZ(radians)
        if (keyboard.isKeyDown(Keys.Up)) body.addForce(body.orientation * Vector3(0F, thrustSpeed, 0F))
        return super.update(seconds)
    }

    init {
        body.data = ObjectTypes.SHIP
        body.boundingSphere = bounds.radius
        simulation.addBody(body)

        addNodes(LeafNode(shipGeometry), GunNode(eventBus, simulation, scheduler, Matrix4.createTranslation(0F, 2F, 0F)))
        addComponents(Physics(body), WrapAround { body.position = it })
    }
}
