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

enum class Keys(val code: String) {
    None(""),
    Backspace("Backspace"),
    Delete("Delete"),
    Enter("Enter"),
    Escape("Escape"),
    Space("Space"),
    Tab("Tab"),
    BackSlash("IntlBackslash"),
    ForwardSlash("Slash"),

    Digit1("Digit1"),
    Digit2("Digit2"),
    Digit3("Digit3"),
    Digit4("Digit4"),
    Digit5("Digit5"),
    Digit6("Digit6"),
    Digit7("Digit7"),
    Digit8("Digit8"),
    Digit9("Digit9"),
    Digit0("Digit0"),

    A("KeyA"),
    B("KeyB"),
    C("KeyC"),
    D("KeyD"),
    E("KeyE"),
    F("KeyF"),
    G("KeyG"),
    H("KeyH"),
    I("KeyI"),
    J("KeyJ"),
    K("KeyK"),
    L("KeyL"),
    M("KeyM"),
    N("KeyN"),
    O("KeyO"),
    P("KeyP"),
    Q("KeyQ"),
    R("KeyR"),
    S("KeyS"),
    T("KeyT"),
    U("KeyU"),
    V("KeyV"),
    W("KeyW"),
    X("KeyX"),
    Y("KeyY"),
    Z("KeyZ"),

    Up("ArrowUp"),
    Down("ArrowDown"),
    Left("ArrowLeft"),
    Right("ArrowRight"),

    Decimal("Period"),
    Minus("Minus"),
    Equal("Equal"),

    LeftAlt("AltLeft"),
    LeftControl("ControlLeft"),
    LeftShift("ShiftLeft"),

    RightAlt("AltRight"),
    RightControl("ControlRight"),
    RightShift("ShiftRight"),

    F1("F1"),
    F2("F2"),
    F3("F3"),
    F4("F4"),
    F5("F5"),
    F6("F6"),
    F7("F7"),
    F8("F8"),
    F9("F9"),
    F10("F10"),
    F11("F11"),
    F12("F12")
}
