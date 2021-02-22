package engine.core

import engine.graph.Geometry
import engine.graph.Renderer
import engine.math.Aabb

class Model(private val parts: Map<Material, Mesh>) : Geometry {

    val meshes = parts.values

    override val vertexCount = parts.values.sumBy { it.vertexCount }

    override val triangleCount = parts.values.sumBy { it.triangleCount }

    override val bounds = meshes.fold(Aabb(), { acc, src -> acc.aggregate(src.bounds) })

    override fun render(renderer: Renderer) = parts.forEach { (material, mesh) ->
        renderer.material = material
        mesh.render(renderer)
    }
}
