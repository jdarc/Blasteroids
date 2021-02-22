package engine.core

import engine.graph.Geometry
import engine.graph.Renderer
import engine.math.Aabb
import engine.math.Vector3

class Mesh(val data: FloatArray, private val count: Int) : Geometry {

    override val vertexCount get() = data.size / ELEMENTS_PER_VERTEX

    override val triangleCount get() = count / VERTICES_PER_FACE

    override val bounds = data.toList().windowed(3, 8).map { (x, y, z) -> Vector3(x, y, z) }.fold(Aabb(), { acc, src -> acc.aggregate(src) })

    override fun render(renderer: Renderer) = renderer.draw(data, count)

    companion object {
        const val ELEMENTS_PER_VERTEX = 8
        const val VERTICES_PER_FACE = 3
    }
}
