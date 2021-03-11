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

package engine.graph

import engine.core.Camera
import engine.core.Color

class Scene {

    var backcolor = Color.BLACK

    val root = object : BranchNode() {
        override val isRoot = true
    }

    fun update(seconds: Float) {
        root.traverseDown({ update(it, seconds) })
        root.traverseUp { postUpdate(it, seconds) }
    }

    fun render(camera: Camera, renderer: Renderer) {
        renderer.resize()
        camera.aspectRatio = renderer.aspectRatio

        renderer.view = camera.view
        renderer.projection = camera.projection
        renderer.clear(backcolor)
        root.traverseDown({ render(it, camera, renderer) }, { postRender(it, renderer) })
    }

    private fun update(node: Node, seconds: Float): Boolean {
        node.components.forEach { it.preUpdate(seconds, node) }
        node.update(seconds)
        node.combineTransforms()
        return true
    }

    private fun postUpdate(node: Node, seconds: Float) {
        node.combineBounds()
        node.components.forEach { it.postUpdate(seconds, node) }
    }

    private fun render(node: Node, camera: Camera, renderer: Renderer): Boolean {
        node.components.forEach { it.preRender(renderer, node) }
        return when {
            camera.frustum.contains(node.bounds) -> {
                node.render(renderer)
                true
            }
            else -> false
        }
    }

    private fun postRender(node: Node, renderer: Renderer) = node.components.forEach { it.postRender(renderer, node) }
}
