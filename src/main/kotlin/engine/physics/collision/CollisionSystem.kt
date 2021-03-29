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

package engine.physics.collision

import engine.physics.RigidBody

@Suppress("MemberVisibilityCanBePrivate")
class CollisionSystem(private val collisionFilter: (RigidBody, RigidBody) -> Boolean, val tolerance: Float = 0.01F) {
    private val narrowPhase = GjkEpaSolver()

    fun detect(bodies: Array<RigidBody>, handler: CollisionHandler) {
        for (body0 in bodies) {
            for (body1 in bodies) {
                if (body0.id < body1.id && collisionFilter(body0, body1) && body0.hitTest(body1)) {
                    val results = narrowPhase.collide(body0.skin, body1.skin, tolerance)
                    if (results.hasCollided) {
                        val r0 = results.r0 - body0.skin.origin
                        val r1 = results.r1 - body1.skin.origin
                        handler.impact(body0, body1, results.normal, r0, r1, results.initialPenetration)
                    }
                }
            }
        }
    }
}
