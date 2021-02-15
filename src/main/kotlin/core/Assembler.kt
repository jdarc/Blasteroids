package core

import math.Vector2
import math.Vector3

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

    fun compile(): Model {
        val count = Mesh.VERTICES_PER_FACE * Mesh.ELEMENTS_PER_VERTEX
        val groups = mutableMapOf<Material, Mesh>()
        for ((material, faces) in faces.groupBy { it.material }.toMap()) {
            val buffer = FloatArray(faces.size * count)
            faces.forEachIndexed { index, face -> pack(face).copyInto(buffer, index * count) }
            groups[materials[material]!!] = Mesh(buffer, faces.size * Mesh.VERTICES_PER_FACE)
        }
        return Model(groups)
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
