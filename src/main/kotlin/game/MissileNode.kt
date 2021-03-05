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

import engine.core.Color
import engine.core.Material
import engine.core.Primitives
import engine.graph.BranchNode
import engine.graph.LeafNode
import engine.graph.components.MaterialComponent
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.Particle

class MissileNode : BranchNode() {
    private val particle = Particle()

    override fun update(seconds: Float): Boolean {
        particle.position = position
        particle.integrate(seconds)
        localTransform = Matrix4.create(particle.position, Matrix4.IDENTITY, Vector3.ONE)
        return super.update(seconds)
    }

    fun fire(origin: Vector3, direction: Vector3) {
        position = origin
        particle.velocity = direction * speed
    }

     init {
        add(LeafNode(MISSILE_GEOMETRY).add(MaterialComponent(MISSILE_MATERIAL)))
     }

    companion object {
        private var speed = 50F

        private val MISSILE_GEOMETRY = Primitives.createSphere(0.2F, 8, 8)

        private val MISSILE_MATERIAL = Material().apply {
            colors.diffuse = Color.RED
            colors.specular = Color.WHITE
        }

        fun build() = MissileNode()
    }
}

