package engine.graph

import engine.core.Camera
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

    fun render(camera: Camera, renderer: Renderer) {
        renderer.resize()
        camera.aspectRatio = renderer.aspectRatio
        renderer.view = camera.view
        renderer.projection = camera.projection

        renderer.clear(backcolor)

        Frustum(camera).run { root.traverseDown { it.isContainedBy(this).apply { it.render(renderer) } } }
    }
}
