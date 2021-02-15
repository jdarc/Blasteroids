package graph

import math.Matrix4

class LeafNode(private val geometry: Geometry, transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {

    override val localBounds get() = geometry.boundingBox

    override fun updateWorldBounds() {
        worldBounds.reset()
        worldBounds.aggregate(localBounds, worldTransform)
    }

    override fun render(renderer: Renderer): Boolean {
        renderer.world = worldTransform
        geometry.render(renderer)
        return true
    }
}
