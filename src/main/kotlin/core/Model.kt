@file:Suppress("unused")

package core

import graph.Geometry
import graph.Renderer
import math.Aabb

class Model(private val parts: Map<Material, Mesh>) : Geometry {
    val materials = parts.keys
    val meshes = parts.values

    override val vertexCount = parts.values.sumBy { it.vertexCount }

    override val triangleCount = parts.values.sumBy { it.triangleCount }

    override val boundingBox = meshes.fold(Aabb(), { acc, src -> acc.aggregate(src.boundingBox) })

    override fun render(renderer: Renderer) {
        parts.forEach { (material, mesh) ->
            renderer.material = material
            mesh.render(renderer)
        }
    }
}
