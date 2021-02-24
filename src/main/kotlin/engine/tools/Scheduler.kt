/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package engine.tools

class Scheduler {
    private val processed = mutableListOf<Command>()
    private val commands = mutableListOf<Command>()
    private var clock = 0F

    fun schedule(delay: Float, command: () -> Unit) {
        commands.add(ScheduledCommand((clock + delay).coerceAtLeast(0F), command))
    }

    fun every(every: Float, until: Float, command: () -> Unit) {
        commands.add(RepeatingCommand((clock + every), every, until, command))
    }

    fun update(timeStep: Float) {
        clock += timeStep

        commands.fold(processed) { dst, command -> if (command.execute(clock)) dst.add(command); dst }
        commands.removeAll(processed)

        processed.forEach { if (it.repeats(clock) && it is RepeatingCommand) every(it.every, it.until, it.command) }
        processed.clear()
    }

    private companion object {
        interface Command {
            fun execute(clock: Float): Boolean
            fun repeats(clock: Float): Boolean
        }

        data class ScheduledCommand(val at: Float, val command: () -> Unit) : Command {
            override fun execute(clock: Float) = if (at <= clock) {
                command()
                true
            } else false

            override fun repeats(clock: Float) = false
        }

        data class RepeatingCommand(val at: Float, val every: Float, val until: Float, val command: () -> Unit) : Command {
            override fun execute(clock: Float) = if (at <= clock) {
                command()
                true
            } else false

            override fun repeats(clock: Float) = clock < until
        }
    }
}
