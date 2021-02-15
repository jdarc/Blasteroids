@file:Suppress("unused")

package core

import io.Keyboard
import io.Keys
import io.Mouse
import math.Vector3
import kotlin.math.*

class FirstPersonControl(private val camera: Camera) {
    private var yaw = 0F
    private var pitch = 0F
    private var mouseX = 0
    private var mouseY = 0

    fun update(seconds: Double, speed: Double) {
        val keyboard = Keyboard.getState()
        val mouse = Mouse.getState()

        yaw = -atan2(-camera.view.m20, camera.view.m22)
        pitch = -asin(camera.view.m21)
        if (mouse.leftButton.isPressed) {
            yaw += (mouseX - mouse.x) * 0.005F
            pitch = min(1.57F, max(-1.57F, pitch + (mouseY - mouse.y) * 0.005F))
        }
        mouseX = mouse.x
        mouseY = mouse.y

        val scaledSpeed = (seconds * speed).toFloat()

        if (keyboard.isKeyDown(Keys.W)) camera.moveForward(scaledSpeed)
        else if (keyboard.isKeyDown(Keys.S)) camera.moveBackward(scaledSpeed)

        if (keyboard.isKeyDown(Keys.A)) camera.moveLeft(scaledSpeed)
        else if (keyboard.isKeyDown(Keys.D)) camera.moveRight(scaledSpeed)

        camera.orient(Vector3(-sin(yaw) * cos(pitch), sin(pitch), -cos(yaw) * cos(pitch)))
    }
}
