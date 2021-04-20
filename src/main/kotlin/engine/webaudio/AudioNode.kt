package engine.webaudio

open external class AudioNode {
    val context: BaseAudioContext

    fun connect(audContext: AudioNode)
}
