package engine.graph

import engine.core.Color
import engine.math.Frustum

class Scene {

    var backcolor = Color.BLACK

    val root = object : BranchNode() {
        override val isRoot = true
    }

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
