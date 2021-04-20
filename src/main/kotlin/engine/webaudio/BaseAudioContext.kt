package engine.webaudio

import org.khronos.webgl.ArrayBuffer

open external class BaseAudioContext {
    val currentTime: Double
    val destination: AudioDestinationNode

    fun createBufferSource(): AudioBufferSourceNode
    fun createGain(): GainNode

    fun decodeAudioData(buffer: ArrayBuffer, success: (buffer: AudioBuffer) -> Unit, failure: (e: Exception) -> Unit)
}
