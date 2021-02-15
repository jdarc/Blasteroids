package graph

import core.Color
import math.Frustum

class Scene {

    var backcolor = Color.BLACK

    val root = BranchNode()

    fun update(seconds: Float) {
        root.traverseDown {
            it.update(seconds)
            it.updateTransform()
            true
        }
        root.traverseUp { it.updateWorldBounds() }
    }

    fun render(frustum: Frustum, renderer: Renderer) {
        renderer.view = frustum.view
        renderer.projection = frustum.projection
        renderer.clear(backcolor)
        root.traverseDown { it.isContainedBy(frustum).apply { it.render(renderer) } }
    }
}
