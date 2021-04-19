package engine.webaudio

open external class AudioNode {
    val gain: AudioParam

    fun connect(audContext: AudioNode)
}
