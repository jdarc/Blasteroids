package engine.webaudio

external class AudioBufferSourceNode : AudioNode {
    var buffer: AudioBuffer
    var loop: Boolean

    fun start()
    fun stop()
}

