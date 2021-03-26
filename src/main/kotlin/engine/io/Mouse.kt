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

package engine.io

import kotlinx.browser.window
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.math.sign

object Mouse {
    private var x = 0
    private var y = 0
    private var scrollWheel = 0
    private var leftButton = ButtonState.Released
    private var middleButton = ButtonState.Released
    private var rightButton = ButtonState.Released
    private var current = MouseState(0, 0, 0, ButtonState.Released, ButtonState.Released, ButtonState.Released)
    private var dirty = true

    val state: MouseState
        get() {
            if (dirty) {
                current = MouseState(x, y, scrollWheel, leftButton, middleButton, rightButton)
                dirty = false
            }
            return current
        }

    private fun captureMouseState(event: MouseEvent) {
        x = event.x.toInt()
        y = event.y.toInt()
        leftButton = if (event.buttons.toInt() and 0x1 != 0) ButtonState.Pressed else ButtonState.Released
        middleButton = if (event.buttons.toInt() and 0x2 != 0) ButtonState.Pressed else ButtonState.Released
        rightButton = if (event.buttons.toInt() and 0x4 != 0) ButtonState.Pressed else ButtonState.Released
        dirty = true
    }

    private fun captureWheelState(event: WheelEvent) = captureMouseState(event).also { scrollWheel += sign(event.deltaY).toInt() }

    init {
        window.addEventListener("mousedown", { captureMouseState(it as MouseEvent) })
        window.addEventListener("mousemove", { captureMouseState(it as MouseEvent) })
        window.addEventListener("mouseup", { captureMouseState(it as MouseEvent) })
        window.addEventListener("wheel", { captureWheelState(it as WheelEvent) })
    }
}
