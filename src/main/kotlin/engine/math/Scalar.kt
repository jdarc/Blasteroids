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

import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import kotlin.math.sqrt
import kotlin.random.Random

object Scalar {
    private val random = Random(window.performance.now().toInt())

    const val PI = 3.14159265359F
    const val EPSILON = 0.00000001F
    const val TINY = 0.00001F
    const val HUGE = 100000F

    fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && abs(a - b) <= epsilon

    fun abs(n: Float) = if (n < 0) -n else n

    fun abs(n: Double) = if (n < 0) -n else n

    fun min(a: Float, b: Float) = if (a < b) a else b

    fun max(a: Float, b: Float) = if (a > b) a else b

    fun invSqrt(n: Float) = 1 / sqrt(n)

    fun hypot(x: Float, y: Float, z: Float) = sqrt(x * x + y * y + z * z)

    fun rnd() = random.nextFloat()

    fun rnd(from: Int = 0, to: Int = Int.MAX_VALUE) = random.nextInt(from, to)

    fun Int.sqr() = this * this

    fun Float.sqr() = this * this

    fun FloatArray.pack() = Float32Array(this.toTypedArray())
}
