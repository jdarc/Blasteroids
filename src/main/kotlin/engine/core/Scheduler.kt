package engine.core

class Scheduler {
    private val processed = mutableListOf<CommandWrapper>()
    private val commands = mutableListOf<CommandWrapper>()
    private var clock = 0F

    fun schedule(delay: Float, command: () -> Unit) {
        commands.add(CommandWrapper((clock + delay).coerceAtLeast(0F), command))
    }

    fun update(timeStep: Float) {
        clock += timeStep
        commands.fold(processed) { dst, wrapper -> if (wrapper.at <= clock) wrapper.command(); dst.add(wrapper); dst }
        commands.removeAll(processed)
        processed.clear()
    }

    private companion object {
        data class CommandWrapper(val at: Float, val command: () -> Unit)
    }
}
