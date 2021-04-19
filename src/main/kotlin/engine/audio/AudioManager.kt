package engine.audio

import engine.webaudio.AudioContext
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

class AudioManager {

    private val context = AudioContext()

    fun readSample(path: String, filename: String): AudioSample {
        val audioSample = AudioSample(this.context)
        GlobalScope.launch {
            val data = window.fetch("$path/$filename").await().arrayBuffer().await()
            context.decodeAudioData(data, { audioSample.buffer = it }, { console.log(it) })
        }
        return audioSample
    }
}
