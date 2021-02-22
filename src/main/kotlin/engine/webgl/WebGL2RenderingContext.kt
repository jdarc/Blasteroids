package engine.webgl

import org.khronos.webgl.WebGLObject
import org.khronos.webgl.WebGLRenderingContext

@Suppress("unused", "PropertyName")
abstract external class WebGL2RenderingContext : WebGLRenderingContext {
    val DEPTH_BUFFER_BIT: Int
    val STENCIL_BUFFER_BIT: Int
    val COLOR_BUFFER_BIT: Int
    val POINTS: Int
    val LINES: Int
    val LINE_LOOP: Int
    val LINE_STRIP: Int
    val TRIANGLES: Int
    val TRIANGLE_STRIP: Int
    val TRIANGLE_FAN: Int
    val ZERO: Int
    val ONE: Int
    val SRC_COLOR: Int
    val ONE_MINUS_SRC_COLOR: Int
    val SRC_ALPHA: Int
    val ONE_MINUS_SRC_ALPHA: Int
    val DST_ALPHA: Int
    val ONE_MINUS_DST_ALPHA: Int
    val DST_COLOR: Int
    val ONE_MINUS_DST_COLOR: Int
    val SRC_ALPHA_SATURATE: Int
    val FUNC_ADD: Int
    val BLEND_EQUATION: Int
    val BLEND_EQUATION_RGB: Int
    val BLEND_EQUATION_ALPHA: Int
    val FUNC_SUBTRACT: Int
    val FUNC_REVERSE_SUBTRACT: Int
    val BLEND_DST_RGB: Int
    val BLEND_SRC_RGB: Int
    val BLEND_DST_ALPHA: Int
    val BLEND_SRC_ALPHA: Int
    val CONSTANT_COLOR: Int
    val ONE_MINUS_CONSTANT_COLOR: Int
    val CONSTANT_ALPHA: Int
    val ONE_MINUS_CONSTANT_ALPHA: Int
    val BLEND_COLOR: Int
    val ARRAY_BUFFER: Int
    val ELEMENT_ARRAY_BUFFER: Int
    val ARRAY_BUFFER_BINDING: Int
    val ELEMENT_ARRAY_BUFFER_BINDING: Int
    val STREAM_DRAW: Int
    val STATIC_DRAW: Int
    val DYNAMIC_DRAW: Int
    val BUFFER_SIZE: Int
    val BUFFER_USAGE: Int
    val CURRENT_VERTEX_ATTRIB: Int
    val FRONT: Int
    val BACK: Int
    val FRONT_AND_BACK: Int
    val CULL_FACE: Int
    val BLEND: Int
    val DITHER: Int
    val STENCIL_TEST: Int
    val DEPTH_TEST: Int
    val SCISSOR_TEST: Int
    val POLYGON_OFFSET_FILL: Int
    val SAMPLE_ALPHA_TO_COVERAGE: Int
    val SAMPLE_COVERAGE: Int
    val NO_ERROR: Int
    val INVALID_ENUM: Int
    val INVALID_VALUE: Int
    val INVALID_OPERATION: Int
    val OUT_OF_MEMORY: Int
    val CW: Int
    val CCW: Int
    val LINE_WIDTH: Int
    val ALIASED_POINT_SIZE_RANGE: Int
    val ALIASED_LINE_WIDTH_RANGE: Int
    val CULL_FACE_MODE: Int
    val FRONT_FACE: Int
    val DEPTH_RANGE: Int
    val DEPTH_WRITEMASK: Int
    val DEPTH_CLEAR_VALUE: Int
    val DEPTH_FUNC: Int
    val STENCIL_CLEAR_VALUE: Int
    val STENCIL_FUNC: Int
    val STENCIL_FAIL: Int
    val STENCIL_PASS_DEPTH_FAIL: Int
    val STENCIL_PASS_DEPTH_PASS: Int
    val STENCIL_REF: Int
    val STENCIL_VALUE_MASK: Int
    val STENCIL_WRITEMASK: Int
    val STENCIL_BACK_FUNC: Int
    val STENCIL_BACK_FAIL: Int
    val STENCIL_BACK_PASS_DEPTH_FAIL: Int
    val STENCIL_BACK_PASS_DEPTH_PASS: Int
    val STENCIL_BACK_REF: Int
    val STENCIL_BACK_VALUE_MASK: Int
    val STENCIL_BACK_WRITEMASK: Int
    val VIEWPORT: Int
    val SCISSOR_BOX: Int
    val COLOR_CLEAR_VALUE: Int
    val COLOR_WRITEMASK: Int
    val UNPACK_ALIGNMENT: Int
    val PACK_ALIGNMENT: Int
    val MAX_TEXTURE_SIZE: Int
    val MAX_VIEWPORT_DIMS: Int
    val SUBPIXEL_BITS: Int
    val RED_BITS: Int
    val GREEN_BITS: Int
    val BLUE_BITS: Int
    val ALPHA_BITS: Int
    val DEPTH_BITS: Int
    val STENCIL_BITS: Int
    val POLYGON_OFFSET_UNITS: Int
    val POLYGON_OFFSET_FACTOR: Int
    val TEXTURE_BINDING_2D: Int
    val SAMPLE_BUFFERS: Int
    val SAMPLES: Int
    val SAMPLE_COVERAGE_VALUE: Int
    val SAMPLE_COVERAGE_INVERT: Int
    val COMPRESSED_TEXTURE_FORMATS: Int
    val DONT_CARE: Int
    val FASTEST: Int
    val NICEST: Int
    val GENERATE_MIPMAP_HINT: Int
    val BYTE: Int
    val UNSIGNED_BYTE: Int
    val SHORT: Int
    val UNSIGNED_SHORT: Int
    val INT: Int
    val UNSIGNED_INT: Int
    val FLOAT: Int
    val DEPTH_COMPONENT: Int
    val ALPHA: Int
    val RGB: Int
    val RGBA: Int
    val LUMINANCE: Int
    val LUMINANCE_ALPHA: Int
    val UNSIGNED_SHORT_4_4_4_4: Int
    val UNSIGNED_SHORT_5_5_5_1: Int
    val UNSIGNED_SHORT_5_6_5: Int
    val FRAGMENT_SHADER: Int
    val VERTEX_SHADER: Int
    val MAX_VERTEX_ATTRIBS: Int
    val MAX_VERTEX_UNIFORM_VECTORS: Int
    val MAX_VARYING_VECTORS: Int
    val MAX_COMBINED_TEXTURE_IMAGE_UNITS: Int
    val MAX_VERTEX_TEXTURE_IMAGE_UNITS: Int
    val MAX_TEXTURE_IMAGE_UNITS: Int
    val MAX_FRAGMENT_UNIFORM_VECTORS: Int
    val SHADER_TYPE: Int
    val DELETE_STATUS: Int
    val LINK_STATUS: Int
    val VALIDATE_STATUS: Int
    val ATTACHED_SHADERS: Int
    val ACTIVE_UNIFORMS: Int
    val ACTIVE_ATTRIBUTES: Int
    val SHADING_LANGUAGE_VERSION: Int
    val CURRENT_PROGRAM: Int
    val NEVER: Int
    val LESS: Int
    val EQUAL: Int
    val LEQUAL: Int
    val GREATER: Int
    val NOTEQUAL: Int
    val GEQUAL: Int
    val ALWAYS: Int
    val KEEP: Int
    val REPLACE: Int
    val INCR: Int
    val DECR: Int
    val INVERT: Int
    val INCR_WRAP: Int
    val DECR_WRAP: Int
    val VENDOR: Int
    val RENDERER: Int
    val VERSION: Int
    val NEAREST: Int
    val LINEAR: Int
    val NEAREST_MIPMAP_NEAREST: Int
    val LINEAR_MIPMAP_NEAREST: Int
    val NEAREST_MIPMAP_LINEAR: Int
    val LINEAR_MIPMAP_LINEAR: Int
    val TEXTURE_MAG_FILTER: Int
    val TEXTURE_MIN_FILTER: Int
    val TEXTURE_WRAP_S: Int
    val TEXTURE_WRAP_T: Int
    val TEXTURE_2D: Int
    val TEXTURE: Int
    val TEXTURE_CUBE_MAP: Int
    val TEXTURE_BINDING_CUBE_MAP: Int
    val TEXTURE_CUBE_MAP_POSITIVE_X: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_X: Int
    val TEXTURE_CUBE_MAP_POSITIVE_Y: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_Y: Int
    val TEXTURE_CUBE_MAP_POSITIVE_Z: Int
    val TEXTURE_CUBE_MAP_NEGATIVE_Z: Int
    val MAX_CUBE_MAP_TEXTURE_SIZE: Int
    val TEXTURE0: Int
    val TEXTURE1: Int
    val TEXTURE2: Int
    val TEXTURE3: Int
    val TEXTURE4: Int
    val TEXTURE5: Int
    val TEXTURE6: Int
    val TEXTURE7: Int
    val TEXTURE8: Int
    val TEXTURE9: Int
    val TEXTURE10: Int
    val TEXTURE11: Int
    val TEXTURE12: Int
    val TEXTURE13: Int
    val TEXTURE14: Int
    val TEXTURE15: Int
    val TEXTURE16: Int
    val TEXTURE17: Int
    val TEXTURE18: Int
    val TEXTURE19: Int
    val TEXTURE20: Int
    val TEXTURE21: Int
    val TEXTURE22: Int
    val TEXTURE23: Int
    val TEXTURE24: Int
    val TEXTURE25: Int
    val TEXTURE26: Int
    val TEXTURE27: Int
    val TEXTURE28: Int
    val TEXTURE29: Int
    val TEXTURE30: Int
    val TEXTURE31: Int
    val ACTIVE_TEXTURE: Int
    val REPEAT: Int
    val CLAMP_TO_EDGE: Int
    val MIRRORED_REPEAT: Int
    val FLOAT_VEC2: Int
    val FLOAT_VEC3: Int
    val FLOAT_VEC4: Int
    val INT_VEC2: Int
    val INT_VEC3: Int
    val INT_VEC4: Int
    val BOOL: Int
    val BOOL_VEC2: Int
    val BOOL_VEC3: Int
    val BOOL_VEC4: Int
    val FLOAT_MAT2: Int
    val FLOAT_MAT3: Int
    val FLOAT_MAT4: Int
    val SAMPLER_2D: Int
    val SAMPLER_CUBE: Int
    val VERTEX_ATTRIB_ARRAY_ENABLED: Int
    val VERTEX_ATTRIB_ARRAY_SIZE: Int
    val VERTEX_ATTRIB_ARRAY_STRIDE: Int
    val VERTEX_ATTRIB_ARRAY_TYPE: Int
    val VERTEX_ATTRIB_ARRAY_NORMALIZED: Int
    val VERTEX_ATTRIB_ARRAY_POINTER: Int
    val VERTEX_ATTRIB_ARRAY_BUFFER_BINDING: Int
    val IMPLEMENTATION_COLOR_READ_TYPE: Int
    val IMPLEMENTATION_COLOR_READ_FORMAT: Int
    val COMPILE_STATUS: Int
    val LOW_FLOAT: Int
    val MEDIUM_FLOAT: Int
    val HIGH_FLOAT: Int
    val LOW_INT: Int
    val MEDIUM_INT: Int
    val HIGH_INT: Int
    val FRAMEBUFFER: Int
    val RENDERBUFFER: Int
    val RGBA4: Int
    val RGB5_A1: Int
    val RGB565: Int
    val DEPTH_COMPONENT16: Int
    val STENCIL_INDEX: Int
    val STENCIL_INDEX8: Int
    val DEPTH_STENCIL: Int
    val RENDERBUFFER_WIDTH: Int
    val RENDERBUFFER_HEIGHT: Int
    val RENDERBUFFER_INTERNAL_FORMAT: Int
    val RENDERBUFFER_RED_SIZE: Int
    val RENDERBUFFER_GREEN_SIZE: Int
    val RENDERBUFFER_BLUE_SIZE: Int
    val RENDERBUFFER_ALPHA_SIZE: Int
    val RENDERBUFFER_DEPTH_SIZE: Int
    val RENDERBUFFER_STENCIL_SIZE: Int
    val FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE: Int
    val FRAMEBUFFER_ATTACHMENT_OBJECT_NAME: Int
    val FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL: Int
    val FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE: Int
    val COLOR_ATTACHMENT0: Int
    val DEPTH_ATTACHMENT: Int
    val STENCIL_ATTACHMENT: Int
    val DEPTH_STENCIL_ATTACHMENT: Int
    val NONE: Int
    val FRAMEBUFFER_COMPLETE: Int
    val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: Int
    val FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: Int
    val FRAMEBUFFER_INCOMPLETE_DIMENSIONS: Int
    val FRAMEBUFFER_UNSUPPORTED: Int
    val FRAMEBUFFER_BINDING: Int
    val RENDERBUFFER_BINDING: Int
    val MAX_RENDERBUFFER_SIZE: Int
    val INVALID_FRAMEBUFFER_OPERATION: Int
    val UNPACK_FLIP_Y_WEBGL: Int
    val UNPACK_PREMULTIPLY_ALPHA_WEBGL: Int
    val CONTEXT_LOST_WEBGL: Int
    val UNPACK_COLORSPACE_CONVERSION_WEBGL: Int
    val BROWSER_DEFAULT_WEBGL: Int

    fun createVertexArray(): WebGLObject
    fun bindVertexArray(vao: WebGLObject?)
}
