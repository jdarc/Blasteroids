package engine.io

enum class ButtonState {
    Pressed,
    Released;

    val isPressed get() = this == Pressed
}
