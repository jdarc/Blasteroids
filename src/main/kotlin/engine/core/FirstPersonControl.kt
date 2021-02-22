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
