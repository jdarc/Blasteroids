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
    private val processed = mutableListOf<CommandWrapper>()
    private val commands = mutableListOf<CommandWrapper>()
    private var clock = 0F

    fun schedule(delay: Float, command: () -> Unit) {
        commands.add(CommandWrapper((clock + delay).coerceAtLeast(0F), command))
    }

    fun update(timeStep: Float) {
        clock += timeStep
        commands.fold(processed) { dst, wrapper -> if (wrapper.at <= clock) dst.add(wrapper); dst }
        processed.forEach { it.command(); }
        commands.removeAll(processed)
        processed.clear()
    }

    private companion object {
        data class CommandWrapper(val at: Float, val command: () -> Unit)
    }
}
