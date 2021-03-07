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

package engine.graph

import engine.math.Aabb
import engine.math.Matrix4

open class BranchNode(transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {
    private val children = mutableListOf<Node>()

    override val bounds get() = children.fold(Aabb(), { dst, src -> dst.aggregate(src.bounds) })

    operator fun get(index: Int) = children[index]

    fun addNodes(vararg nodes: Node): BranchNode {
        nodes.forEach {
            if (it != this && it !in children) {
                children.add(it)
                it.parent = this
            }
        }
        return this
    }

    fun removeNodes(vararg nodes: Node): BranchNode {
        nodes.forEach {
            if (it in children) {
                children.remove(it)
                it.parent = null
            }
        }
        return this
    }

    override fun traverseDown(apply: (Node) -> Boolean, pre: (Node) -> Unit, post: (Node) -> Unit) {
        pre(this)
        if (apply(this)) children.forEach { it.traverseDown(apply, pre, post) }
        post(this)
    }

    override fun traverseUp(apply: (Node) -> Unit) {
        children.forEach { it.traverseUp(apply) }
        apply(this)
    }

    override fun aggregateBounds() {
        aggregatedBounds.reset()
        children.forEach { aggregatedBounds.aggregate(it.aggregatedBounds) }
    }
}
