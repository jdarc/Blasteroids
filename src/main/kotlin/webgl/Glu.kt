@file:Suppress("unused")

package webgl

import core.Color
import math.Vector3
import org.khronos.webgl.*

@Suppress("MemberVisibilityCanBePrivate")
object Glu {
    var anisotropicExt: dynamic = null
    var anisotropicMax = -1F

    fun compileShader(gl: WebGL2RenderingContext, shaderSource: String, shaderType: Int): WebGLShader {
        val shader = gl.createShader(shaderType)
        gl.shaderSource(shader, shaderSource)
        gl.compileShader(shader)
        val success = gl.getShaderParameter(shader, gl.COMPILE_STATUS) as Boolean
        if (!success) throw RuntimeException("shader failed to compile: ${gl.getShaderInfoLog(shader)}")
        return shader ?: throw RuntimeException("failed to create shader")
    }

    fun createProgram(gl: WebGL2RenderingContext, vertexShader: WebGLShader, fragmentShader: WebGLShader): WebGLProgram {
        val program = gl.createProgram()
        gl.attachShader(program, vertexShader)
        gl.attachShader(program, fragmentShader)
        gl.linkProgram(program)
        val success = gl.getProgramParameter(program, gl.LINK_STATUS) as Boolean
        if (!success) throw RuntimeException("program failed to link: ${gl.getProgramInfoLog(program)}")
        return program ?: throw RuntimeException("failed to create program")
    }

    fun createProgramFromSource(gl: WebGL2RenderingContext, vertexSource: String, fragmentSource: String): WebGLProgram {
        val vs = compileShader(gl, vertexSource, gl.VERTEX_SHADER)
        val fs = compileShader(gl, fragmentSource, gl.FRAGMENT_SHADER)
        return createProgram(gl, vs, fs)
    }

    fun createImageTexture(gl: WebGL2RenderingContext, image: TexImageSource): WebGLTexture {
        val texture = gl.createTexture()
        gl.bindTexture(gl.TEXTURE_2D, texture)
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, 1)
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image)
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR)
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_LINEAR)
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE)
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE)
        gl.generateMipmap(gl.TEXTURE_2D)
        configureAnisotropic(gl)
        return texture ?: throw RuntimeException("failed to create image texture")
    }

    fun extractAttributeLocations(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val count = gl.getProgramParameter(program, gl.ACTIVE_ATTRIBUTES) as Int
        for (i in 0 until count) {
            val info = gl.getActiveAttrib(program, i)!!
            result[info.name] = gl.getAttribLocation(program, info.name)
        }
        return result
    }

    fun extractUniformLocations(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, WebGLUniformLocation> {
        val result = mutableMapOf<String, WebGLUniformLocation>()
        val count = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS) as Int
        for (i in 0 until count) {
            val info = gl.getActiveUniform(program, i)!!
            result[info.name] = gl.getUniformLocation(program, info.name)!!
        }
        return result
    }

    fun createVertexBuffer(gl: WebGL2RenderingContext, data: Float32Array): WebGLBuffer {
        val buffer = gl.createBuffer()
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer)
        gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW)
        return buffer ?: throw RuntimeException("failed to create buffer")
    }

    fun WebGL2RenderingContext.uniformVec3(loc: WebGLUniformLocation?, v: Vector3) = uniform3f(loc, v.x, v.y, v.z)

    fun WebGL2RenderingContext.uniformColor(loc: WebGLUniformLocation?, c: Color) = uniform3f(loc, c.red, c.grn, c.blu)

    fun WebGL2RenderingContext.bindTexture2D(unit: Int, loc: WebGLUniformLocation?, texture: WebGLTexture?) {
        activeTexture(WebGLRenderingContext.TEXTURE0 + unit)
        uniform1i(loc, unit)
        bindTexture(WebGLRenderingContext.TEXTURE_2D, texture)
    }

    private fun configureAnisotropic(gl: WebGL2RenderingContext) {
        if (anisotropicMax < 0) {
            anisotropicExt = gl.getExtension("EXT_texture_filter_anisotropic")
            anisotropicMax = (gl.getParameter(anisotropicExt.MAX_TEXTURE_MAX_ANISOTROPY_EXT as Int) ?: 0) as Float
        }
        if (anisotropicExt != null && anisotropicMax > 0) {
            gl.texParameterf(gl.TEXTURE_2D, anisotropicExt.TEXTURE_MAX_ANISOTROPY_EXT as Int, anisotropicMax)
        }
    }
}
