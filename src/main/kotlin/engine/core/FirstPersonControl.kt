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

import engine.io.Keyboard
import engine.io.Keys
import engine.io.Mouse
import engine.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
class FirstPersonControl(private val camera: Camera) {
    private var mouseX = 0
    private var mouseY = 0

    fun update(seconds: Float, moveFactor: Float = 25F, lookFactor: Float = 0.25F) {
        val keyboard = Keyboard.getState()
        val mouse = Mouse.getState()

        if (mouse.leftButton.isPressed) {
            val lookRate = seconds * lookFactor
            val yaw = camera.yaw + (mouseX - mouse.x) * lookRate
            val pitch = (camera.pitch + (mouseY - mouse.y) * lookRate).coerceIn(-1.57F, 1.57F)
            camera.orient(Vector3(-sin(yaw) * cos(pitch), sin(pitch), -cos(yaw) * cos(pitch)))
        }

        val moveRate = seconds * moveFactor
        if (keyboard.isKeyDown(Keys.W)) camera.moveForward(moveRate)
        else if (keyboard.isKeyDown(Keys.S)) camera.moveBackward(moveRate)
        if (keyboard.isKeyDown(Keys.A)) camera.moveLeft(moveRate)
        else if (keyboard.isKeyDown(Keys.D)) camera.moveRight(moveRate)

        mouseX = mouse.x
        mouseY = mouse.y
    }
}
