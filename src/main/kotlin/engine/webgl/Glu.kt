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

package engine.webgl

import engine.core.Color
import engine.math.Vector3
import org.khronos.webgl.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Glu {
    var anisotropicExt: dynamic = null
    var anisotropicMax = -1F

    fun WebGL2RenderingContext.compileShader(shaderSource: String, shaderType: Int): WebGLShader {
        val shader = createShader(shaderType)
        shaderSource(shader, shaderSource)
        compileShader(shader)
        val success = getShaderParameter(shader, COMPILE_STATUS) as Boolean
        if (!success) throw RuntimeException("shader failed to compile: ${getShaderInfoLog(shader)}")
        return shader ?: throw RuntimeException("failed to create shader")
    }

    fun WebGL2RenderingContext.createProgram(vertexShader: WebGLShader, fragmentShader: WebGLShader): WebGLProgram {
        val program = createProgram()
        attachShader(program, vertexShader)
        attachShader(program, fragmentShader)
        linkProgram(program)
        val success = getProgramParameter(program, LINK_STATUS) as Boolean
        if (!success) throw RuntimeException("program failed to link: ${getProgramInfoLog(program)}")
        return program ?: throw RuntimeException("failed to create program")
    }

    fun WebGL2RenderingContext.createProgramFromSource(vertexSource: String, fragmentSource: String): WebGLProgram {
        val vs = compileShader(vertexSource, VERTEX_SHADER)
        val fs = compileShader(fragmentSource, FRAGMENT_SHADER)
        return createProgram(vs, fs)
    }

    fun WebGL2RenderingContext.createImageTexture(image: TexImageSource): WebGLTexture {
        val texture = createTexture()
        bindTexture(TEXTURE_2D, texture)
        pixelStorei(UNPACK_FLIP_Y_WEBGL, 1)
        texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, image)
        texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)
        texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR_MIPMAP_LINEAR)
        texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, REPEAT)
        texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, REPEAT)
        generateMipmap(TEXTURE_2D)
        configureAnisotropic(this)
        return texture ?: throw RuntimeException("failed to create image texture")
    }

    fun WebGL2RenderingContext.extractAttributeLocations(program: WebGLProgram) =
        (0 until getProgramParameter(program, ACTIVE_ATTRIBUTES) as Int).map { getActiveAttrib(program, it) }
            .requireNoNulls().associateBy({ it.name }, { getAttribLocation(program, it.name) })

    fun WebGL2RenderingContext.extractUniformLocations(program: WebGLProgram): Map<String, WebGLUniformLocation> {
        return (0 until getProgramParameter(program, ACTIVE_UNIFORMS) as Int).map { getActiveUniform(program, it) }
            .requireNoNulls().associateBy({ it.name }, { getUniformLocation(program, it.name)!! })
    }

    fun WebGL2RenderingContext.createVertexBuffer(data: Float32Array): WebGLBuffer {
        val buffer = createBuffer()
        bindBuffer(ARRAY_BUFFER, buffer)
        bufferData(ARRAY_BUFFER, data, STATIC_DRAW)
        return buffer ?: throw RuntimeException("failed to create buffer")
    }

    fun WebGL2RenderingContext.uniformVec3(loc: WebGLUniformLocation?, v: Vector3) = uniform3f(loc, v.x, v.y, v.z)

    fun WebGL2RenderingContext.uniformColor(loc: WebGLUniformLocation?, c: Color) = uniform3f(loc, c.red, c.green, c.blue)

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
