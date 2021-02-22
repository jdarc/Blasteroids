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

import engine.core.Camera
import engine.graph.BranchNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.*
import engine.physics.Particle

class ShipNode(private val camera: Camera) : BranchNode() {
    private var angularSpeed = Scalar.PI
    private var thrustSpeed = 20F
    private var radians = 0F
    private val particle = Particle().apply { dampingFactor = 0.5F }

    override fun update(seconds: Float): Boolean {
        val keyboard = Keyboard.getState()

        when {
            keyboard.isKeyDown(Keys.Left) -> radians += angularSpeed * seconds
            keyboard.isKeyDown(Keys.Right) -> radians -= angularSpeed * seconds
        }
        val rotation = Matrix4.createRotationZ(radians)

        particle.clearForces()
        if (keyboard.isKeyDown(Keys.Up)) {
            particle.addForce(rotation * Vector3(0F, thrustSpeed, 0F))
        }
        particle.integrate(seconds)

        localTransform = Matrix4.create(particle.position, rotation, Vector3.ONE)

        wrapAround()

        return super.update(seconds)
    }

    private fun wrapAround() {
        val frustum = Frustum(camera)
        if (!frustum.contains(worldBounds)) {
            val top = frustum.intersect(Ray(Vector3.ZERO, Vector3.UNIT_Y))
            val right = frustum.intersect(Ray(Vector3.ZERO, Vector3.UNIT_X))
            val bot = frustum.intersect(Ray(Vector3.ZERO, -Vector3.UNIT_Y))
            val left = frustum.intersect(Ray(Vector3.ZERO, -Vector3.UNIT_X))
            var (x, y, z) = particle.position
            if (x < left.x) x = right.x else if (x > right.x) x = left.x
            if (y < bot.y) y = top.y else if (y > top.y) y = bot.y
            particle.position = Vector3(x, y, z)
        }
    }
}
