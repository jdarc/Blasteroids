package engine.io

import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

object Keyboard {
    private val mapper = Keys.values().map { Pair(it.code, it) }.toMap()
    private val state = BooleanArray(Keys.values().size)
    private val capture = BooleanArray(Keys.values().size)

    fun getState() = KeyBoardState(state.copyInto(capture))

    init {
        window.addEventListener("keydown", {
            it as KeyboardEvent
            state[mapper[it.code]?.ordinal ?: 0] = true
        })

        window.addEventListener("keyup", {
            it as KeyboardEvent
            state[mapper[it.code]?.ordinal ?: 0] = false
        })
    }
}
