/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package engine.core

import engine.graph.Geometry
import engine.graph.Renderer
import engine.math.Aabb
import engine.math.Vector3

class Mesh(private val data: FloatArray, private val count: Int) : Geometry {

    override val vertexCount = data.size / ELEMENTS_PER_VERTEX

    override val triangleCount = count / VERTICES_PER_FACE

    override val vertices = data.toList().windowed(3, 8) { (x, y, z) -> Vector3(x, y, z) }.toTypedArray()

    override val bounds = data.toList().windowed(3, 8).fold(Aabb(), { acc, src -> acc.aggregate(src[0], src[1], src[2]) })

    override fun render(renderer: Renderer) = renderer.draw(data, count)

    companion object {
        const val ELEMENTS_PER_VERTEX = 8
        const val VERTICES_PER_FACE = 3
    }
}
