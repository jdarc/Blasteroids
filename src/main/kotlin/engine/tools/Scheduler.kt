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
    private val rescheduled = mutableListOf<ScheduledCommand>()
    private val processed = mutableListOf<ScheduledCommand>()
    private val commands = mutableListOf<ScheduledCommand>()
    private var clock = 0F

    fun schedule(delay: Float, command: () -> Unit) = schedule(0F, 0F, delay, command)

    fun schedule(every: Float, duration: Float, delay: Float = every, command: () -> Unit) {
        commands.add(RepeatingCommand((clock + delay).coerceAtLeast(0F), every, clock + duration, command))
    }

    fun update(timeStep: Float) {
        clock += timeStep

        commands.forEach {
            if (it.executes(clock)) {
                processed.add(it)
                if (it.repeats(clock)) rescheduled.add(it.build(clock))
            }
        }

        commands.apply {
            removeAll(processed)
            addAll(rescheduled)
        }

        processed.clear()
        rescheduled.clear()
    }

    private companion object {
        open class ScheduledCommand(val at: Float, val command: () -> Unit) {
            fun executes(clock: Float) = (at <= clock).apply { if (this) command() }

            open fun repeats(clock: Float) = false
            open fun build(clock: Float) = ScheduledCommand(clock + at, command)
        }

        class RepeatingCommand(at: Float, val every: Float, val until: Float, command: () -> Unit) : ScheduledCommand(at, command) {
            override fun repeats(clock: Float) = clock < until
            override fun build(clock: Float) = RepeatingCommand((clock + every), every, until, command)
        }
    }
}
