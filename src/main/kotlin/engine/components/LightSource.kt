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

package engine.components

import engine.core.Color
import engine.graph.Component
import engine.graph.Node
import engine.graph.Renderer
import engine.math.Frustum
import engine.math.Vector3

class LightSource(private val index: Int, var color: Color, var position: Vector3 = Vector3.ZERO) : Component {
    var emitting = true

    override fun preRender(frustum: Frustum, renderer: Renderer, node: Node) {
        val light = renderer.lights[index]
        if (emitting) {
            light.on = true
            light.color = color
            light.position = position + Vector3(node.combinedTransform.m03, node.combinedTransform.m13, node.combinedTransform.m23)
        }
    }

    override fun postRender(frustum: Frustum, renderer: Renderer, node: Node) {
        renderer.lights[index].on = false
    }
}
