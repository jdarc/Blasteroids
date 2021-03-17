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
import engine.math.Frustum
import engine.math.Matrix4
import engine.math.Vector3

abstract class Node(var transform: Matrix4 = Matrix4.IDENTITY) {
    var combinedTransform = Matrix4.IDENTITY

    abstract fun combineBounds()
    abstract val localBounds: Aabb
    val bounds = Aabb()

    val components = mutableSetOf<Component>()

    var position
        get() = Vector3(transform.m03, transform.m13, transform.m23)
        set(value) {
            transform = Matrix4.createTranslation(value - position) * transform
        }

    var transformedPosition
        get() = Vector3(combinedTransform.m03, combinedTransform.m13, combinedTransform.m23)
        set(value) {
            combinedTransform = Matrix4.createTranslation(value - transformedPosition) * combinedTransform
        }

    var parent: BranchNode? = null
        set(value) {
            if (value == parent) return
            field?.removeNodes(this)
            field = value
            field?.addNodes(this)
        }

    open val isRoot = false

    val root
        get(): BranchNode? {
            var root = parent
            while (root?.isRoot != true) root = root?.parent
            return root
        }

    fun isContainedBy(frustum: Frustum) = frustum.contains(bounds)

    fun addComponents(vararg components: Component): Node {
        this.components.addAll(components)
        return this
    }

    fun removeComponents(vararg components: Component): Node {
        this.components.removeAll(components)
        return this
    }

    fun removeAllComponents(): Node {
        this.components.clear()
        return this
    }

    open fun traverseDown(visitor: (Node) -> Boolean, apply: (Node) -> Unit = {}) {
        visitor(this)
        apply(this)
    }

    open fun traverseUp(visitor: (Node) -> Unit) = visitor(this)
    
    open fun combineTransforms() {
        combinedTransform = (parent?.combinedTransform ?: Matrix4.IDENTITY) * transform
    }

    open fun update(seconds: Float) = true

    open fun render(renderer: Renderer) = true
}
