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

import engine.graph.Renderer
import engine.math.Matrix4
import engine.math.Scalar.pack
import engine.math.Vector3
import engine.webgl.Glu
import engine.webgl.WebGL2RenderingContext
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLTexture
import org.w3c.dom.HTMLCanvasElement

class Device(canvas: HTMLCanvasElement) : Renderer {
    private val gl = canvas.getContext("webgl2") as WebGL2RenderingContext
    private val buffers = mutableMapOf<FloatArray, WebGLBuffer>()
    private val textures = mutableMapOf<Texture, WebGLTexture>()

    private lateinit var activeProgram: Program

    override val isReady get() = this::activeProgram.isInitialized

    override val aspectRatio get() = gl.drawingBufferWidth.toFloat() / gl.drawingBufferHeight.toFloat()

    suspend fun initialize() {
        gl.disable(gl.STENCIL_TEST)
        gl.enable(gl.DEPTH_TEST)
        gl.enable(gl.CULL_FACE)
        gl.cullFace(gl.BACK)
        gl.depthFunc(gl.LEQUAL)

        val vertexSrc = window.fetch("shaders/vertex.glsl").await().text().await()
        val fragmentSrc = window.fetch("shaders/fragment.glsl").await().text().await()
        activeProgram = Program(gl, vertexSrc, fragmentSrc)
        activeProgram.use()
        ambience = 0.1F

        buffers.clear()
        textures.clear()
    }

    override val lights: Array<Light> = (0..7).map { PointLight(it, this) }.toTypedArray()

    override var ambience = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            activeProgram.setAmbientIntensity(field)
        }

    override var material = Material.DEFAULT
        set(value) {
            field = value
            val colors = field.colors
            activeProgram.setAmbientColor(colors.ambient)
            activeProgram.setDiffuseColor(colors.diffuse)
            activeProgram.setSpecularColor(colors.specular)
            activeProgram.setShininess(field.shininess)

            val maps = field.textures
            activeProgram.setAmbientTexture(textures.getOrPut(maps.ambient, { Glu.createImageTexture(gl, maps.ambient.source) }))
            activeProgram.setDiffuseTexture(textures.getOrPut(maps.diffuse, { Glu.createImageTexture(gl, maps.diffuse.source) }))
            activeProgram.setSpecularTexture(textures.getOrPut(maps.specular, { Glu.createImageTexture(gl, maps.specular.source) }))
        }

    override var world = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.setWorldMatrix(field)
            activeProgram.setNormalMatrix(Matrix4.transpose(Matrix4.invert(field)))
        }

    override var view = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.setViewMatrix(field)
            activeProgram.setCameraPosition(Matrix4.invert(field).run { Vector3(m03, m13, m23) })
        }

    override var projection = Matrix4.IDENTITY
        set(value) {
            field = value
            activeProgram.setProjectionMatrix(field)
        }

    override fun resize(): Boolean {
        val displayWidth = gl.canvas.clientWidth
        val displayHeight = gl.canvas.clientHeight
        if (gl.canvas.width != displayWidth || gl.canvas.height != displayHeight) {
            gl.canvas.width = displayWidth
            gl.canvas.height = displayHeight
            gl.viewport(0, 0, displayWidth, displayHeight)
            return true
        }
        return false
    }

    override fun clear(color: Color) {
        gl.clearColor(color.red, color.green, color.blue, color.alpha)
        gl.clear(gl.COLOR_BUFFER_BIT or gl.DEPTH_BUFFER_BIT)
    }

    override fun draw(data: FloatArray, count: Int) {
        activeProgram.bindVertexBuffer(buffers.getOrPut(data, { Glu.createVertexBuffer(gl, data.pack()) }))
        gl.drawArrays(gl.TRIANGLES, 0, count)
    }

    companion object {
        private class PointLight(val index: Int, val device: Device) : Light {
            override var position = Vector3.ZERO
                set(value) {
                    field = value
                    device.activeProgram.setLightPosition(index, field)
                }

            override var color = Color.BLACK
                set(value) {
                    field = value
                    device.activeProgram.setLightColor(index, field)
                }

            override var on = false
                set(value) {
                    field = value
                    device.activeProgram.toggleLight(index, if (field) 1 else 0)
                }
        }
    }
}
