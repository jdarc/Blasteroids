package engine.graph

import engine.math.Aabb

interface Geometry : Renderable {
    val vertexCount: Int
    val triangleCount: Int
    val bounds: Aabb
}
