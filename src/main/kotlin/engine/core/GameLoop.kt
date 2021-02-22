@file:Suppress("unused")

package engine.core

import kotlinx.browser.window

class GameLoop(private val game: Game) {
    private var running = false
    private var tock = 0.0

    fun start() {
        if (!running) {
            running = true
            window.requestAnimationFrame { tock = it; loop(it) }
        }
    }

    fun stop() {
        running = false
    }

    private fun loop(tick: Double) {
        val interval = tick - tock; tock = tick
        game.update((interval / 1000.0).toFloat())
        game.render()
        if (running) window.requestAnimationFrame { loop(it) }
    }
}

