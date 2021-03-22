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
import engine.math.Vector2
import engine.math.Vector3

class Assembler {
    private val vertices = mutableListOf<Vector3>()
    private val normals = mutableListOf<Vector3>()
    private val texCoords = mutableListOf<Vector2>()
    private val faces = mutableListOf<Face>()
    private val materials = mutableMapOf(Pair("", Material.DEFAULT))
    private var currentMaterial = ""

    fun addMaterial(material: Material) {
        materials[material.name] = material
    }

    fun useMaterial(name: String) {
        currentMaterial = name
    }

    fun addVertex(x: Float, y: Float, z: Float) {
        vertices.add(Vector3(x, y, z))
    }

    fun addNormal(x: Float, y: Float, z: Float) {
        normals.add(Vector3.normalize(Vector3(x, y, z)))
    }

    fun addTextureCoordinate(u: Float, v: Float) {
        texCoords.add(Vector2(u, v))
    }

    fun addFace(v: IntArray, vn: IntArray, vt: IntArray) {
        faces.add(Face(v, vn, vt, currentMaterial))
    }

    fun center() {
        val cog = vertices.fold(Vector3.ZERO, { acc, cur -> acc + cur }) / vertices.size.toFloat()
        val centered = vertices.map { it - cog }
        vertices.clear()
        vertices.addAll(centered)
    }

    fun build(type: BuildType = BuildType.Model): Geometry {
        val count = Mesh.VERTICES_PER_FACE * Mesh.ELEMENTS_PER_VERTEX
        return when (type) {
            BuildType.Mesh -> {
                val buffer = FloatArray(faces.size * count)
                faces.forEachIndexed { index, face -> pack(face).copyInto(buffer, index * count) }
                Mesh(buffer, buffer.size / Mesh.ELEMENTS_PER_VERTEX)
            }
            BuildType.Model -> {
                val groups = mutableMapOf<Material, Mesh>()
                for ((material, faces) in faces.groupBy { it.material }.toMap()) {
                    val buffer = FloatArray(faces.size * count)
                    faces.forEachIndexed { index, face -> pack(face).copyInto(buffer, index * count) }
                    groups[materials[material]!!] = Mesh(buffer, buffer.size / Mesh.ELEMENTS_PER_VERTEX)
                }
                Model(groups)
            }
        }
    }

    private fun pack(face: Face): FloatArray {
        val v0 = vertices[face.v[0]]; val vn0 = normals[face.vn[0]]; val vt0 = texCoords[face.vt[0]]
        val v1 = vertices[face.v[1]]; val vn1 = normals[face.vn[1]]; val vt1 = texCoords[face.vt[1]]
        val v2 = vertices[face.v[2]]; val vn2 = normals[face.vn[2]]; val vt2 = texCoords[face.vt[2]]
        return floatArrayOf(
            v0.x, v0.y, v0.z, vn0.x, vn0.y, vn0.z, vt0.x, vt0.y,
            v1.x, v1.y, v1.z, vn1.x, vn1.y, vn1.z, vt1.x, vt1.y,
            v2.x, v2.y, v2.z, vn2.x, vn2.y, vn2.z, vt2.x, vt2.y
        )
    }

    companion object {
        private class Face(val v: IntArray, val vn: IntArray, val vt: IntArray, val material: String)
    }
}
