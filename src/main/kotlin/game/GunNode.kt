/*
 * Copyright (c) 2020 Jean d'Arc.
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
import engine.core.Scheduler
import engine.graph.BranchNode
import engine.graph.MaterialNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.Matrix4
import engine.math.Vector3

class GunNode(private val scheduler: Scheduler, transform: Matrix4) : BranchNode(transform) {
    private var fireRate = 0.1F
    private var speed = 50F
    private var timer = 0F
    private var last = 0F

    override fun update(seconds: Float): Boolean {
        timer += seconds
        if (Keyboard.getState().isKeyDown(Keys.S)) fire()
        return super.update(seconds)
    }

    private fun fire() {
        if (timer - last < fireRate) return
        root?.apply {
            val missile = spawnMissile()
            add(missile)
            scheduler.schedule(1F) { remove(missile) }
        }
        last = timer
    }

    private fun spawnMissile(): BranchNode {
        val position = Vector3(worldTransform.m03, worldTransform.m13, worldTransform.m23)
        val velocity = Vector3(-worldTransform.m10, worldTransform.m00, 0F) * speed
        return MaterialNode(MISSILE_MATERIAL).add(MissileNode(MISSILE).apply {
            particle.position = position
            particle.velocity = velocity
        })
    }

    private companion object {
        val MISSILE_MATERIAL = Material("missile").apply {
            colors.diffuse = Color.RED
            colors.specular = Color.WHITE
        }
        val MISSILE = Primitives.createSphere(0.2F, 8, 8)
    }
}
