@file:Suppress("unused")

package io

class MouseState(
    val x: Int,
    val y: Int,
    val scrollWheel: Int,
    val leftButton: ButtonState,
    val middleButton: ButtonState,
    val rightButton: ButtonState
)
