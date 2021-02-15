package io

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

    fun getState() = MouseState(x, y, scrollWheel, leftButton, middleButton, rightButton)

    private fun captureMouseState(event: MouseEvent) {
        x = event.x.toInt()
        y = event.y.toInt()
        leftButton = if (event.buttons.toInt() and 0x1 != 0) ButtonState.Pressed else ButtonState.Released
        middleButton = if (event.buttons.toInt() and 0x2 != 0) ButtonState.Pressed else ButtonState.Released
        rightButton = if (event.buttons.toInt() and 0x4 != 0) ButtonState.Pressed else ButtonState.Released
    }

    private fun captureWheelState(event: WheelEvent) {
        captureMouseState(event)
        scrollWheel += sign(event.deltaY).toInt()
    }

    init {
        window.addEventListener("mousedown", { captureMouseState(it as MouseEvent) })
        window.addEventListener("mousemove", { captureMouseState(it as MouseEvent) })
        window.addEventListener("mouseup", { captureMouseState(it as MouseEvent) })
        window.addEventListener("wheel", { captureWheelState(it as WheelEvent) })
    }
}
