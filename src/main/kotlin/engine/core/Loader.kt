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

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope

@Suppress("SpellCheckingInspection")
object Loader {

    suspend fun read(path: String, filename: String) = coroutineScope {
        val factory = Assembler()
        val directives = window.fetch("$path/$filename").await().text().await()
        directives.split('\n').map { it.trim() }.filter { it.isNotEmpty() && it[0] != '#' }.forEach { line ->
            val fragments = line.trim().split(' ')
            when {
                fragments[0].equals("mtllib", true) -> readMaterials(path, fragments[1]).forEach { factory.addMaterial(it.value) }
                fragments[0].equals("usemtl", true) -> factory.useMaterial(fragments[1])
                fragments[0].equals("v", true) -> {
                    val x = fragments[1].trim().toFloat()
                    val y = fragments[2].trim().toFloat()
                    val z = fragments[3].trim().toFloat()
                    factory.addVertex(x, y, z)
                }
                fragments[0].equals("vn", true) -> {
                    val x = fragments[1].trim().toFloat()
                    val y = fragments[2].trim().toFloat()
                    val z = fragments[3].trim().toFloat()
                    factory.addNormal(x, y, z)
                }
                fragments[0].equals("vt", true) -> {
                    val u = fragments[1].trim().toFloat()
                    val v = fragments[2].trim().toFloat()
                    factory.addTextureCoordinate(u, v)
                }
                fragments[0].equals("f", true) -> {
                    val i0 = fragments[1].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val i1 = fragments[2].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val i2 = fragments[3].split("/").map { it.trim().toInt() - 1 }.toIntArray()
                    val vx = intArrayOf(i0[0], i1[0], i2[0])
                    val vt = intArrayOf(i0[1], i1[1], i2[1])
                    val vn = intArrayOf(i0[2], i1[2], i2[2])
                    factory.addFace(vx, vn, vt)
                }
            }
        }
        factory.build()
    }

    private suspend fun readMaterials(path: String, filename: String) = coroutineScope {
        var currentMaterial: Material? = null
        val materials = mutableMapOf<String, Material>()
        val directives = window.fetch("$path/$filename").await().text().await()
        directives.split('\n').map { it.trim() }.filter { it.isNotEmpty() && it[0] != '#' }.forEach { line ->
            when {
                line.startsWith("newmtl", true) -> {
                    val name = line.trim().split(' ')[1]
                    currentMaterial = Material(name)
                    materials[name] = currentMaterial!!
                }
                line.startsWith("ka", true) -> currentMaterial?.colors?.ambient = parseColor(line)
                line.startsWith("kd", true) -> currentMaterial?.colors?.diffuse = parseColor(line)
                line.startsWith("ks", true) -> currentMaterial?.colors?.specular = parseColor(line)
                line.startsWith("ns", true) -> currentMaterial?.shininess = line.split(' ')[1].trim().toFloat()
                line.startsWith("map_ka", true) -> currentMaterial?.textures?.ambient = Texture.fetch("$path/${line.trim().split(' ')[1]}")
                line.startsWith("map_kd", true) -> currentMaterial?.textures?.diffuse = Texture.fetch("$path/${line.trim().split(' ')[1]}")
                line.startsWith("map_ks", true) -> currentMaterial?.textures?.specular = Texture.fetch("$path/${line.trim().split(' ')[1]}")
            }
        }
        materials
    }

    private fun parseColor(line: String) = line.trim().split(' ').drop(1).map { it.toFloat() }.windowed(3) {
        Color.create(it[0], it[1], it[2])
    }.first()
}
