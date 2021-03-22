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

package engine.physics.geometry

import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3
import engine.physics.collision.CollisionShape

abstract class Shape : CollisionShape {

    override var origin = Vector3.ZERO

    override var basis = Matrix4.IDENTITY

    open val boundingSphere get() = Scalar.HUGE

    var restitution = 0.2F
        set(value) {
            field = value.coerceIn(0F, 1F)
        }

    var friction = 0.5F
        set(value) {
            field = value.coerceIn(0F, 1F)
        }

    open fun calculateBodyInertia(mass: Float) = Matrix4.createScale(0.4F * mass * boundingSphere * boundingSphere)
}
