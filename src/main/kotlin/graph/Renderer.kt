package graph

import core.Color
import core.Light
import core.Material
import math.Matrix4

interface Renderer {
    val isReady: Boolean
    val aspectRatio: Float

    val lights: Array<Light>

    var ambience: Float
    var material: Material

    var world: Matrix4
    var view: Matrix4
    var projection: Matrix4

    fun resize(): Boolean
    fun clear(color: Color)
    fun draw(data: FloatArray, count: Int)
}
