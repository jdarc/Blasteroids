package engine.graph

import engine.math.Matrix4

open class LeafNode(private val geometry: Geometry, transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {

    override val localBounds = geometry.bounds

    override fun updateWorldBounds() {
        worldBounds.reset().aggregate(localBounds, worldTransform)
    }

    override fun render(renderer: Renderer): Boolean {
        renderer.world = worldTransform
        geometry.render(renderer)
        return true
    }
}
