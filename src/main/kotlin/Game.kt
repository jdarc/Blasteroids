@file:Suppress("SpellCheckingInspection")

import game.Blasteroids
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

suspend fun main() = Blasteroids(document.querySelector("canvas") as HTMLCanvasElement).run()
