package engine.webaudio

external class AudioBuffer {
    /** Returns a float representing the sample rate, in samples per second, of the PCM data stored in the buffer. */
    val sampleRate: Float

    /** Returns an integer representing the length, in sample-frames, of the PCM data stored in the buffer. */
    val length: Int

    /** Returns a double representing the duration, in seconds, of the PCM data stored in the buffer. */
    val duration: Double

    /** Returns an integer representing the number of discrete audio channels described by the PCM data stored in the buffer. */
    val numberOfChannels: Int
}
