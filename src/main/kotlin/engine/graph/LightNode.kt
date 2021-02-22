package engine.graph

import engine.core.Color
import engine.math.Aabb
import engine.math.Matrix4
import engine.math.Vector3

@Suppress("MemberVisibilityCanBePrivate")
class LightNode(val index: Int, var color: Color, transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {

    var emitting = true

    override val localBounds = Aabb()

    override fun updateWorldBounds() {
        worldBounds.reset().aggregate(localBounds)
    }

    override fun render(renderer: Renderer): Boolean {
        val light = renderer.lights[index]
        light.on = emitting
        light.color = color
        light.position = Vector3(worldTransform.m03, worldTransform.m13, worldTransform.m23)
        return true
    }
}
