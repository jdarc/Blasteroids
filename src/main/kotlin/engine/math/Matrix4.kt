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

package engine.math

import org.khronos.webgl.Float32Array
import org.khronos.webgl.set
import kotlin.math.*

@Suppress("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate")
data class Matrix4(
    val m00: Float, val m10: Float, val m20: Float, val m30: Float,
    val m01: Float, val m11: Float, val m21: Float, val m31: Float,
    val m02: Float, val m12: Float, val m22: Float, val m32: Float,
    val m03: Float, val m13: Float, val m23: Float, val m33: Float
) {

    operator fun unaryMinus() = Matrix4(
        -m00, -m10, -m20, -m30,
        -m01, -m11, -m21, -m31,
        -m02, -m12, -m22, -m32,
        -m03, -m13, -m23, -m33
    )

    operator fun plus(rhs: Float) = Matrix4(
        m00 + rhs, m10 + rhs, m20 + rhs, m30 + rhs,
        m01 + rhs, m11 + rhs, m21 + rhs, m31 + rhs,
        m02 + rhs, m12 + rhs, m22 + rhs, m32 + rhs,
        m03 + rhs, m13 + rhs, m23 + rhs, m33 + rhs
    )

    operator fun plus(rhs: Matrix4) = Matrix4(
        m00 + rhs.m00, m10 + rhs.m10, m20 + rhs.m20, m30 + rhs.m30,
        m01 + rhs.m01, m11 + rhs.m11, m21 + rhs.m21, m31 + rhs.m31,
        m02 + rhs.m02, m12 + rhs.m12, m22 + rhs.m22, m32 + rhs.m32,
        m03 + rhs.m03, m13 + rhs.m13, m23 + rhs.m23, m33 + rhs.m33
    )

    operator fun minus(rhs: Float) = Matrix4(
        m00 - rhs, m10 - rhs, m20 - rhs, m30 - rhs,
        m01 - rhs, m11 - rhs, m21 - rhs, m31 - rhs,
        m02 - rhs, m12 - rhs, m22 - rhs, m32 - rhs,
        m03 - rhs, m13 - rhs, m23 - rhs, m33 - rhs
    )

    operator fun minus(rhs: Matrix4) = Matrix4(
        m00 - rhs.m00, m10 - rhs.m10, m20 - rhs.m20, m30 - rhs.m30,
        m01 - rhs.m01, m11 - rhs.m11, m21 - rhs.m21, m31 - rhs.m31,
        m02 - rhs.m02, m12 - rhs.m12, m22 - rhs.m22, m32 - rhs.m32,
        m03 - rhs.m03, m13 - rhs.m13, m23 - rhs.m23, m33 - rhs.m33
    )

    operator fun times(rhs: Float) = Matrix4(
        m00 * rhs, m10 * rhs, m20 * rhs, m30 * rhs,
        m01 * rhs, m11 * rhs, m21 * rhs, m31 * rhs,
        m02 * rhs, m12 * rhs, m22 * rhs, m32 * rhs,
        m03 * rhs, m13 * rhs, m23 * rhs, m33 * rhs
    )

    operator fun times(rhs: Vector3): Vector3 {
        val x = rhs.x * m00 + rhs.y * m01 + rhs.z * m02 + m03
        val y = rhs.x * m10 + rhs.y * m11 + rhs.z * m12 + m13
        val z = rhs.x * m20 + rhs.y * m21 + rhs.z * m22 + m23
        val w = rhs.x * m30 + rhs.y * m31 + rhs.z * m32 + m33
        return Vector3(x / w, y / w, z / w)
    }

    operator fun times(rhs: Matrix4) = Matrix4(
        (m00 * rhs.m00) + (m01 * rhs.m10) + (m02 * rhs.m20) + (m03 * rhs.m30),
        (m10 * rhs.m00) + (m11 * rhs.m10) + (m12 * rhs.m20) + (m13 * rhs.m30),
        (m20 * rhs.m00) + (m21 * rhs.m10) + (m22 * rhs.m20) + (m23 * rhs.m30),
        (m30 * rhs.m00) + (m31 * rhs.m10) + (m32 * rhs.m20) + (m33 * rhs.m30),
        (m00 * rhs.m01) + (m01 * rhs.m11) + (m02 * rhs.m21) + (m03 * rhs.m31),
        (m10 * rhs.m01) + (m11 * rhs.m11) + (m12 * rhs.m21) + (m13 * rhs.m31),
        (m20 * rhs.m01) + (m21 * rhs.m11) + (m22 * rhs.m21) + (m23 * rhs.m31),
        (m30 * rhs.m01) + (m31 * rhs.m11) + (m32 * rhs.m21) + (m33 * rhs.m31),
        (m00 * rhs.m02) + (m01 * rhs.m12) + (m02 * rhs.m22) + (m03 * rhs.m32),
        (m10 * rhs.m02) + (m11 * rhs.m12) + (m12 * rhs.m22) + (m13 * rhs.m32),
        (m20 * rhs.m02) + (m21 * rhs.m12) + (m22 * rhs.m22) + (m23 * rhs.m32),
        (m30 * rhs.m02) + (m31 * rhs.m12) + (m32 * rhs.m22) + (m33 * rhs.m32),
        (m00 * rhs.m03) + (m01 * rhs.m13) + (m02 * rhs.m23) + (m03 * rhs.m33),
        (m10 * rhs.m03) + (m11 * rhs.m13) + (m12 * rhs.m23) + (m13 * rhs.m33),
        (m20 * rhs.m03) + (m21 * rhs.m13) + (m22 * rhs.m23) + (m23 * rhs.m33),
        (m30 * rhs.m03) + (m31 * rhs.m13) + (m32 * rhs.m23) + (m33 * rhs.m33)
    )

    fun toArray(dst: Float32Array = Float32Array(16), offset: Int = 0) = dst.apply {
        this[offset + 0x0] = m00; this[offset + 0x1] = m10; this[offset + 0x2] = m20; this[offset + 0x3] = m30
        this[offset + 0x4] = m01; this[offset + 0x5] = m11; this[offset + 0x6] = m21; this[offset + 0x7] = m31
        this[offset + 0x8] = m02; this[offset + 0x9] = m12; this[offset + 0xA] = m22; this[offset + 0xB] = m32
        this[offset + 0xC] = m03; this[offset + 0xD] = m13; this[offset + 0xE] = m23; this[offset + 0xF] = m33
    }

    companion object {
        val IDENTITY = Matrix4(1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F)

        fun equals(lhs: Matrix4, rhs: Matrix4, epsilon: Float = Scalar.EPSILON) =
            Scalar.equals(lhs.m00, rhs.m00, epsilon) && Scalar.equals(lhs.m10, rhs.m10, epsilon) &&
            Scalar.equals(lhs.m20, rhs.m20, epsilon) && Scalar.equals(lhs.m30, rhs.m30, epsilon) &&
            Scalar.equals(lhs.m01, rhs.m01, epsilon) && Scalar.equals(lhs.m11, rhs.m11, epsilon) &&
            Scalar.equals(lhs.m21, rhs.m21, epsilon) && Scalar.equals(lhs.m31, rhs.m31, epsilon) &&
            Scalar.equals(lhs.m02, rhs.m02, epsilon) && Scalar.equals(lhs.m12, rhs.m12, epsilon) &&
            Scalar.equals(lhs.m22, rhs.m22, epsilon) && Scalar.equals(lhs.m32, rhs.m32, epsilon) &&
            Scalar.equals(lhs.m03, rhs.m03, epsilon) && Scalar.equals(lhs.m13, rhs.m13, epsilon) &&
            Scalar.equals(lhs.m23, rhs.m23, epsilon) && Scalar.equals(lhs.m33, rhs.m33, epsilon)

        fun transpose(mat: Matrix4) = Matrix4(
            mat.m00, mat.m01, mat.m02, mat.m03,
            mat.m10, mat.m11, mat.m12, mat.m13,
            mat.m20, mat.m21, mat.m22, mat.m23,
            mat.m30, mat.m31, mat.m32, mat.m33
        )

        fun invert(mat: Matrix4): Matrix4 {
            val b00 = mat.m00 * mat.m11 - mat.m10 * mat.m01
            val b01 = mat.m00 * mat.m21 - mat.m20 * mat.m01
            val b02 = mat.m00 * mat.m31 - mat.m30 * mat.m01
            val b03 = mat.m10 * mat.m21 - mat.m20 * mat.m11
            val b04 = mat.m10 * mat.m31 - mat.m30 * mat.m11
            val b05 = mat.m20 * mat.m31 - mat.m30 * mat.m21
            val b06 = mat.m02 * mat.m13 - mat.m12 * mat.m03
            val b07 = mat.m02 * mat.m23 - mat.m22 * mat.m03
            val b08 = mat.m02 * mat.m33 - mat.m32 * mat.m03
            val b09 = mat.m12 * mat.m23 - mat.m22 * mat.m13
            val b10 = mat.m12 * mat.m33 - mat.m32 * mat.m13
            val b11 = mat.m22 * mat.m33 - mat.m32 * mat.m23
            val det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06
            if (abs(det) < Scalar.EPSILON) throw RuntimeException("matrix is not invertible")
            val invDet = 1F / det
            val a = (b11 * mat.m11 - b10 * mat.m21 + b09 * mat.m31) * invDet
            val b = (b10 * mat.m20 - b11 * mat.m10 - b09 * mat.m30) * invDet
            val c = (b05 * mat.m13 - b04 * mat.m23 + b03 * mat.m33) * invDet
            val d = (b04 * mat.m22 - b05 * mat.m12 - b03 * mat.m32) * invDet
            val e = (b08 * mat.m21 - b11 * mat.m01 - b07 * mat.m31) * invDet
            val f = (b11 * mat.m00 - b08 * mat.m20 + b07 * mat.m30) * invDet
            val g = (b02 * mat.m23 - b05 * mat.m03 - b01 * mat.m33) * invDet
            val h = (b05 * mat.m02 - b02 * mat.m22 + b01 * mat.m32) * invDet
            val i = (b10 * mat.m01 - b08 * mat.m11 + b06 * mat.m31) * invDet
            val j = (b08 * mat.m10 - b10 * mat.m00 - b06 * mat.m30) * invDet
            val k = (b04 * mat.m03 - b02 * mat.m13 + b00 * mat.m33) * invDet
            val l = (b02 * mat.m12 - b04 * mat.m02 - b00 * mat.m32) * invDet
            val m = (b07 * mat.m11 - b09 * mat.m01 - b06 * mat.m21) * invDet
            val n = (b09 * mat.m00 - b07 * mat.m10 + b06 * mat.m20) * invDet
            val o = (b01 * mat.m13 - b03 * mat.m03 - b00 * mat.m23) * invDet
            val p = (b03 * mat.m02 - b01 * mat.m12 + b00 * mat.m22) * invDet
            return Matrix4(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
        }

        fun create(position: Vector3, rotation: Matrix4, scale: Vector3) =
            createTranslation(position) * rotation * createScale(scale)

        fun createTranslation(vec: Vector3) = createTranslation(vec.x, vec.y, vec.z)
        fun createTranslation(x: Float, y: Float, z: Float) =
            Matrix4(1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F, x, y, z, 1F)

        fun createScale(scale: Float) = createScale(scale, scale, scale)
        fun createScale(vec: Vector3) = createScale(vec.x, vec.y, vec.z)
        fun createScale(x: Float, y: Float, z: Float) =
            Matrix4(x, 0F, 0F, 0F, 0F, y, 0F, 0F, 0F, 0F, z, 0F, 0F, 0F, 0F, 1F)

        fun createRotationX(radians: Float) = createFromAxisAngle(1F, 0F, 0F, radians)
        fun createRotationY(radians: Float) = createFromAxisAngle(0F, 1F, 0F, radians)
        fun createRotationZ(radians: Float) = createFromAxisAngle(0F, 0F, 1F, radians)

        fun createFromAxisAngle(axis: Vector3, radians: Float) = createFromAxisAngle(axis.x, axis.y, axis.z, radians)
        fun createFromAxisAngle(x: Float, y: Float, z: Float, radians: Float): Matrix4 {
            val magnitude = sqrt(x * x + y * y + z * z)
            if (magnitude < Scalar.EPSILON) throw RuntimeException("create from axis and angle failed, vector length too small")
            val ax = x / magnitude
            val ay = y / magnitude
            val az = z / magnitude
            val cos = cos(radians)
            val sin = sin(radians)
            val t = 1F - cos
            val xz = ax * az
            val xy = ax * ay
            val yz = ay * az
            val m00 = t * ax * ax + cos
            val m01 = t * xy - az * sin
            val m02 = t * xz + ay * sin
            val m10 = t * xy + az * sin
            val m11 = t * ay * ay + cos
            val m12 = t * yz - ax * sin
            val m20 = t * xz - ay * sin
            val m21 = t * yz + ax * sin
            val m22 = t * az * az + cos
            return Matrix4(m00, m10, m20, 0F, m01, m11, m21, 0F, m02, m12, m22, 0F, 0F, 0F, 0F, 1F)
        }

        fun createFromYawPitchRoll(yaw: Float, pitch: Float, roll: Float): Matrix4 {
            val sr = sin(roll * 0.5F)
            val cr = cos(roll * 0.5F)
            val sp = sin(pitch * 0.5F)
            val cp = cos(pitch * 0.5F)
            val sy = sin(yaw * 0.5F)
            val cy = cos(yaw * 0.5F)
            val qx = cy * sp * cr + sy * cp * sr
            val qy = sy * cp * cr - cy * sp * sr
            val qz = cy * cp * sr - sy * sp * cr
            val qw = cy * cp * cr + sy * sp * sr
            val m00 = 1F - 2F * qy * qy - 2F * qz * qz
            val m01 = 2F * (qx * qy - qw * qz)
            val m02 = 2F * (qx * qz + qw * qy)
            val m10 = 2F * (qx * qy + qw * qz)
            val m11 = 1F - 2F * qx * qx - 2F * qz * qz
            val m12 = 2F * (qy * qz - qw * qx)
            val m20 = 2F * (qx * qz - qw * qy)
            val m21 = 2F * (qy * qz + qw * qx)
            val m22 = 1F - 2F * qx * qx - 2F * qy * qy
            return Matrix4(m00, m10, m20, 0F, m01, m11, m21, 0F, m02, m12, m22, 0F, 0F, 0F, 0F, 1F)
        }

        fun createLookAt(eye: Vector3, at: Vector3, up: Vector3): Matrix4 {
            val d = Vector3.normalize(eye - at)
            val r = Vector3.normalize(Vector3.cross(up, d))
            val u = Vector3.cross(d, r)
            val x = -Vector3.dot(r, eye)
            val y = -Vector3.dot(u, eye)
            val z = -Vector3.dot(d, eye)
            return Matrix4(r.x, u.x, d.x, 0F, r.y, u.y, d.y, 0F, r.z, u.z, d.z, 0F, x, y, z, 1F)
        }

        fun createPerspectiveFov(fov: Float, aspectRatio: Float, near: Float, far: Float): Matrix4 {
            val m11 = tan(Scalar.PI * 0.5F - 0.5F * fov)
            val m00 = m11 / aspectRatio
            val m22 = (near + far) / (near - far)
            val m23 = (near * far) / (near - far) * 2F
            return Matrix4(m00, 0F, 0F, 0F, 0F, m11, 0F, 0F, 0F, 0F, m22, -1F, 0F, 0F, m23, 0F)
        }
    }
}
