package engine.graph

import engine.core.Color
import engine.core.Light
import engine.core.Material
import engine.math.Matrix4

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
