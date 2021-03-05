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

package engine.graph.components

import engine.graph.Component
import engine.graph.Node
import engine.graph.Renderer
import engine.math.Frustum
import engine.math.Ray
import engine.math.Vector3

class WrapAroundComponent : Component() {

    override fun preRender(frustum: Frustum, renderer: Renderer, node: Node) {
        if (!frustum.contains(node.worldBounds)) {
            var (x, y, z) = node.worldPosition
            val top = frustum.intersect(RAY_UP)
            val left = frustum.intersect(RAY_LEFT)
            val right = frustum.intersect(RAY_RIGHT)
            val bottom = frustum.intersect(RAY_DOWN)
            if (x < left.x) x = right.x else if (x > right.x) x = left.x
            if (y < bottom.y) y = top.y else if (y > top.y) y = bottom.y
            node.position = Vector3(x, y, z)
        }
    }

    private companion object {
        val RAY_LEFT = Ray(Vector3.ZERO, -Vector3.UNIT_X)
        val RAY_RIGHT = Ray(Vector3.ZERO, Vector3.UNIT_X)
        val RAY_DOWN = Ray(Vector3.ZERO, -Vector3.UNIT_Y)
        val RAY_UP = Ray(Vector3.ZERO, Vector3.UNIT_Y)
    }
}
