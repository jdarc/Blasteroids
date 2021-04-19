package engine.audio

import engine.webaudio.AudioBuffer
import engine.webaudio.AudioContext

class AudioSample(private val context: AudioContext) {
    lateinit var buffer: AudioBuffer

    fun play(loop: Boolean = false): Playback {
        if (this::buffer.isInitialized) {
            val bufferSource = context.createBufferSource()
            bufferSource.buffer = buffer
            bufferSource.loop = loop

            val gainNode = context.createGain()
            bufferSource.connect(gainNode)
            gainNode.connect(context.destination)

            bufferSource.start()

            return object : Playback {
                override var volume = 1F
                    set(value) {
                        field = value.coerceIn(0F, 1F)
                        gainNode.gain.setValueAtTime(field, context.currentTime)
                    }

                override fun stop() {
                    bufferSource.stop()
                }
            }
        }
        return EMPTY_PLAYBACK
    }

    private companion object {
        val EMPTY_PLAYBACK = object : Playback {
            override var volume = 0F
            override fun stop() = Unit
        }
    }
}
