package core

import graph.Renderer
import kotlinx.browser.window
import kotlinx.coroutines.await
import math.Matrix4
import math.Scalar.pack
import math.Vector3
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLTexture
import webgl.Glu
import webgl.WebGL2RenderingContext

class Device(private val gl: WebGL2RenderingContext) : Renderer {
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
            activeProgram.setModelViewMatrix(view * field)
            activeProgram.setNormalMatrix(Matrix4.transpose(Matrix4.invert(field)))
        }

    override var view = Matrix4.IDENTITY

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
        gl.clearColor(color.red, color.grn, color.blu, color.alpha)
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
                    device.activeProgram.setLightPosition(index, device.view * field)
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
