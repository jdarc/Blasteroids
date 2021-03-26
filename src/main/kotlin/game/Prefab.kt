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

package game

import engine.graph.Geometry
import engine.math.Vector3
import engine.physics.geometry.Hull
import engine.tools.convexhull.HullMaker
import engine.tools.convexhull.Point3D

class Prefab(val geometry: Geometry) {
    private val hullPoints = fromVertexBuffer(HullMaker().build(toVertexBuffer(geometry.vertices)).vertices)

    fun generateHull(scale: Float = 1F) = Hull(hullPoints, scale)

    private companion object {
        fun toVertexBuffer(vertices: Array<Vector3>) = vertices.map { (x, y, z) -> Point3D(x, y, z) }.toTypedArray()
        fun fromVertexBuffer(points: Array<Point3D>) = points.map { (x, y, z) -> Vector3(x, y, z) }.toTypedArray()
    }
}
