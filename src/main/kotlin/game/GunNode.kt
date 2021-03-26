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

import engine.graph.BranchNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.Simulation
import engine.tools.Scheduler

class GunNode(
    private val eventBus: EventBus,
    private val simulation: Simulation,
    private val scheduler: Scheduler,
    transform: Matrix4
) : BranchNode(transform) {
    private val arena by lazy { root?.get(0) as BranchNode }
    private var fireRate = 0.1F
    private var timer = 0F
    private var last = 0F

    override fun update(seconds: Float): Boolean {
        timer += seconds
        if (Keyboard.state.isKeyDown(Keys.S)) fire()
        return super.update(seconds)
    }

    private fun fire() {
        if (timer - last < fireRate) return
        val missile = MissileNode(eventBus, simulation)
        missile.fire(transformedPosition, Vector3(-combinedTransform.m10, combinedTransform.m00, 0F))
        arena.addNodes(missile)
        scheduler.schedule(2F) { missile.destroy() }
        last = timer
    }
}
