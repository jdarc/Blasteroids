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

import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3

@Suppress("unused", "DuplicatedCode")
object Primitives {

    fun createPlane(size: Float): Mesh {
        val assembler = Assembler()

        val halfSize = size / 2F

        assembler.addVertex(-halfSize, 0F, halfSize)
        assembler.addVertex(halfSize, 0F, halfSize)
        assembler.addVertex(halfSize, 0F, -halfSize)
        assembler.addVertex(-halfSize, 0F, -halfSize)

        assembler.addNormal(0F, 1F, 0F)

        assembler.addTextureCoordinate(0F, 0F)
        assembler.addTextureCoordinate(1F, 0F)
        assembler.addTextureCoordinate(1F, 1F)
        assembler.addTextureCoordinate(0F, 1F)

        assembler.addFace(intArrayOf(0, 1, 2), intArrayOf(0, 0, 0), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(2, 3, 0), intArrayOf(0, 0, 0), intArrayOf(2, 3, 0))

        return assembler.build(BuildType.Mesh) as Mesh
    }

    fun createCube(size: Float): Mesh {
        val assembler = Assembler()

        val halfSize = size / 2F

        assembler.addVertex(-halfSize, halfSize, -halfSize)
        assembler.addVertex(halfSize, halfSize, -halfSize)
        assembler.addVertex(halfSize, -halfSize, -halfSize)
        assembler.addVertex(-halfSize, -halfSize, -halfSize)
        assembler.addVertex(halfSize, halfSize, halfSize)
        assembler.addVertex(-halfSize, halfSize, halfSize)
        assembler.addVertex(-halfSize, -halfSize, halfSize)
        assembler.addVertex(halfSize, -halfSize, halfSize)

        assembler.addNormal(0F, 0F, -1F)
        assembler.addNormal(1F, 0F, 0F)
        assembler.addNormal(0F, 0F, 1F)
        assembler.addNormal(-1F, 0F, 0F)
        assembler.addNormal(0F, 1F, 0F)
        assembler.addNormal(0F, -1F, 0F)

        assembler.addTextureCoordinate(0F, 0F)
        assembler.addTextureCoordinate(1F, 0F)
        assembler.addTextureCoordinate(1F, 1F)
        assembler.addTextureCoordinate(0F, 1F)

        assembler.addFace(intArrayOf(0, 1, 2), intArrayOf(0, 0, 0), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(2, 3, 0), intArrayOf(0, 0, 0), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(1, 4, 7), intArrayOf(1, 1, 1), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(7, 2, 1), intArrayOf(1, 1, 1), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(4, 5, 6), intArrayOf(2, 2, 2), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(6, 7, 4), intArrayOf(2, 2, 2), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(0, 3, 6), intArrayOf(3, 3, 3), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(6, 5, 0), intArrayOf(3, 3, 3), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(0, 5, 4), intArrayOf(4, 4, 4), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(4, 1, 0), intArrayOf(4, 4, 4), intArrayOf(2, 3, 0))
        assembler.addFace(intArrayOf(3, 2, 7), intArrayOf(5, 5, 5), intArrayOf(0, 1, 2))
        assembler.addFace(intArrayOf(7, 6, 3), intArrayOf(5, 5, 5), intArrayOf(2, 3, 0))

        return assembler.build(BuildType.Mesh) as Mesh
    }

    fun createSphere(radius: Float, stacks: Int, slices: Int): Mesh {
        val assembler = Assembler()

        val stackAngle = Scalar.PI / stacks
        val sliceAngle = Scalar.PI / slices * 2.0F

        val curve = mutableListOf<Vector3>()
        for (stack in 0..stacks) curve.add(Matrix4.createRotationZ(stackAngle * stack) * Vector3(0F, 1F, 0F))

        for (slice in 0..slices) {
            val aboutY = Matrix4.createRotationY(sliceAngle * slice)
            for ((v, point) in curve.withIndex()) {
                val vertex = aboutY * point
                assembler.addVertex(vertex.x * radius, vertex.y * radius, vertex.z * radius)
                assembler.addNormal(vertex.x, vertex.y, vertex.z)
                assembler.addTextureCoordinate(slice / slices.toFloat(), v / curve.size.toFloat())
            }
        }

        for (slice in 0 until slices) {
            for (stack in 0 until stacks) {
                val ma = stack + slice * (stacks + 1)
                val mb = stack + slice * (stacks + 1) + 1
                val mc = stack + slice * (stacks + 1) + (stacks + 1) + 1
                val md = stack + slice * (stacks + 1) + (stacks + 1)
                assembler.addFace(intArrayOf(md, ma, mb), intArrayOf(md, ma, mb), intArrayOf(md, ma, mb))
                assembler.addFace(intArrayOf(mb, mc, md), intArrayOf(mb, mc, md), intArrayOf(mb, mc, md))
            }
        }

        return assembler.build(BuildType.Mesh) as Mesh
    }
}
