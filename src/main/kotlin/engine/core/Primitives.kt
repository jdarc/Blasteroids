@file:Suppress("unused", "DuplicatedCode", "SpellCheckingInspection")

package engine.core

import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3

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

        return assembler.compile().meshes.first()
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

        return assembler.compile().meshes.first()
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
            val tindex = slice * (stacks + 1)
            for (stack in 0 until stacks) {
                val ma = stack + tindex
                val mb = stack + tindex + 1
                val mc = stack + tindex + (stacks + 1) + 1
                val md = stack + tindex + (stacks + 1)
                assembler.addFace(intArrayOf(md, ma, mb), intArrayOf(md, ma, mb), intArrayOf(md, ma, mb))
                assembler.addFace(intArrayOf(mb, mc, md), intArrayOf(mb, mc, md), intArrayOf(mb, mc, md))
            }
        }

        return assembler.compile().meshes.first()
    }
}
