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

class Model(private val parts: Map<Material, Mesh>) : Geometry {

    override val vertexCount = parts.values.sumOf { it.vertexCount }

    override val triangleCount = parts.values.sumOf { it.triangleCount }

    override val vertices = parts.values.fold(arrayOf<Vector3>()) { acc, mesh -> acc + mesh.vertices }

    override val bounds = parts.values.fold(Aabb()) { acc, src -> acc.aggregate(src.bounds) }

    override fun render(renderer: Renderer) = parts.forEach { (material, mesh) ->
        renderer.material = material
        mesh.render(renderer)
    }
}
