@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package core

import kotlinx.coroutines.channels.Channel
import org.w3c.dom.Image

class Texture private constructor(val source: Image) {

    companion object {
        val DEFAULT = Texture(Image().apply {
            src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAD0lEQVQYlWP4TwAwjAwFAIS1/wGYKmMjAAAAAElFTkSuQmCC"
        })

        suspend fun fetch(filename: String): Texture {
            val channel = Channel<Texture>(0)
            val image = Image()
            image.addEventListener("load", { channel.offer(Texture(image)) })
            image.src = filename
            return channel.receive()
        }
    }
}
