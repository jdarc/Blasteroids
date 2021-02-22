package engine.core

import engine.math.Matrix4
import engine.math.Vector3
import engine.webgl.Glu
import engine.webgl.Glu.bindTexture2D
import engine.webgl.Glu.uniformColor
import engine.webgl.Glu.uniformVec3
import engine.webgl.WebGL2RenderingContext
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLTexture

class Program(private val gl: WebGL2RenderingContext, vertexShaderSrc: String, fragmentShaderSrc: String) {
    private val shaderProgram = Glu.createProgramFromSource(gl, vertexShaderSrc, fragmentShaderSrc)

    private val transformUniforms = TransformUniforms(gl, shaderProgram)
    private val colorUniforms = MaterialUniforms(gl, shaderProgram, "u_colors")
    private val textureUniforms = MaterialUniforms(gl, shaderProgram, "u_textures")

    private val ambienceUniform = gl.getUniformLocation(shaderProgram, "u_ambientIntensity")
    private val shininessUniform = gl.getUniformLocation(shaderProgram, "u_shininess")
    private val cameraUniform = gl.getUniformLocation(shaderProgram, "u_camera_position")

    private val positionAttribute = gl.getAttribLocation(shaderProgram, "a_position")
    private val normalAttribute = gl.getAttribLocation(shaderProgram, "a_normal")
    private val uvAttribute = gl.getAttribLocation(shaderProgram, "a_uv")

    private val lightUniforms = (0 until 8).map { LightUniforms(gl, shaderProgram, it) }

    private var workspace = Float32Array(16)

    fun toggleLight(index: Int, on: Int) = gl.uniform1i(lightUniforms[index].on, on)

    fun setLightPosition(index: Int, position: Vector3) = gl.uniformVec3(lightUniforms[index].position, position)

    fun setLightColor(index: Int, color: Color) = gl.uniformColor(lightUniforms[index].color, color)

    fun setAmbientIntensity(intensity: Float) = gl.uniform1f(ambienceUniform, intensity)

    fun setAmbientColor(color: Color) = gl.uniformColor(colorUniforms.ambient, color)

    fun setDiffuseColor(color: Color) = gl.uniformColor(colorUniforms.diffuse, color)

    fun setSpecularColor(color: Color) = gl.uniformColor(colorUniforms.specular, color)

    fun setAmbientTexture(texture: WebGLTexture) = gl.bindTexture2D(0, textureUniforms.ambient, texture)

    fun setDiffuseTexture(texture: WebGLTexture) = gl.bindTexture2D(1, textureUniforms.diffuse, texture)

    fun setSpecularTexture(texture: WebGLTexture) = gl.bindTexture2D(2, textureUniforms.specular, texture)

    fun setShininess(shininess: Float) = gl.uniform1f(shininessUniform, shininess)

    fun setCameraPosition(vector: Vector3) = gl.uniformVec3(cameraUniform, vector)

    fun setWorldMatrix(matrix: Matrix4) = gl.uniformMatrix4fv(transformUniforms.world, false, matrix.toArray(workspace))

    fun setNormalMatrix(matrix: Matrix4) = gl.uniformMatrix4fv(transformUniforms.normal, false, matrix.toArray(workspace))

    fun setViewMatrix(matrix: Matrix4) = gl.uniformMatrix4fv(transformUniforms.view, false, matrix.toArray(workspace))

    fun setProjectionMatrix(matrix: Matrix4) = gl.uniformMatrix4fv(transformUniforms.projection, false, matrix.toArray(workspace))

    fun use() {
        gl.useProgram(shaderProgram)
        gl.enableVertexAttribArray(positionAttribute)
        gl.enableVertexAttribArray(normalAttribute)
        gl.enableVertexAttribArray(uvAttribute)
    }

    fun bindVertexBuffer(buffer: WebGLBuffer) {
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(positionAttribute, 3, gl.FLOAT, false, 32, 0)
        gl.vertexAttribPointer(normalAttribute, 3, gl.FLOAT, false, 32, 12)
        gl.vertexAttribPointer(uvAttribute, 2, gl.FLOAT, false, 32, 24)
    }

    companion object {
        private class TransformUniforms(gl: WebGL2RenderingContext, program: WebGLProgram) {
            val world = gl.getUniformLocation(program, "u_transforms.world")
            val normal = gl.getUniformLocation(program, "u_transforms.normal")
            val view = gl.getUniformLocation(program, "u_transforms.view")
            val projection = gl.getUniformLocation(program, "u_transforms.projection")
        }

        private class MaterialUniforms(gl: WebGL2RenderingContext, program: WebGLProgram, name: String) {
            val ambient = gl.getUniformLocation(program, "${name}.ambient")
            val diffuse = gl.getUniformLocation(program, "${name}.diffuse")
            val specular = gl.getUniformLocation(program, "${name}.specular")
        }

        private class LightUniforms(gl: WebGL2RenderingContext, program: WebGLProgram, index: Int) {
            val position = gl.getUniformLocation(program, "u_lights[${index}].position")
            val color = gl.getUniformLocation(program, "u_lights[${index}].color")
            val on = gl.getUniformLocation(program, "u_lights[${index}].on")
        }
    }
}
