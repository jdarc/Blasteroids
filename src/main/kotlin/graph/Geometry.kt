package graph

import math.Aabb

interface Geometry : Renderable {
    val vertexCount: Int
    val triangleCount: Int

    val boundingBox: Aabb
}
