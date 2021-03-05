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

abstract class Node(transform: Matrix4 = Matrix4.IDENTITY) {
    private val components = mutableListOf<Component>()

    var localTransform = transform
    var worldTransform = Matrix4.IDENTITY

    abstract val localBounds: Aabb
    val worldBounds = Aabb()

    fun isContainedBy(frustum: Frustum) = frustum.contains(worldBounds)

    var position
        get() = Vector3(localTransform.m03, localTransform.m13, localTransform.m23)
        set(value) {
            localTransform = Matrix4.createTranslation(value - position) * localTransform
        }

    var worldPosition
        get() = Vector3(worldTransform.m03, worldTransform.m13, worldTransform.m23)
        set(value) {
            worldTransform = Matrix4.createTranslation(value - worldPosition) * worldTransform
        }

    var parent: BranchNode? = null
        set(value) {
            if (value == parent) return
            field?.remove(this)
            field = value
            field?.add(this)
        }

    open val isRoot = false

    val root
        get(): BranchNode? {
            var root = parent
            while (root?.isRoot != true) root = root?.parent
            return root
        }

    fun add(vararg components: Component): Node {
        this.components.addAll(components)
        return this
    }

    fun remove(vararg components: Component): Node {
        this.components.removeAll(components)
        return this
    }

    open fun traverseDown(visitor: (Node) -> Boolean, pre: (Node) -> Unit = {},  post: (Node) -> Unit = {}) {
        pre(this)
        visitor(this)
        post(this)
    }

    open fun traverseUp(visitor: (Node) -> Unit) = visitor(this)

    open fun updateTransform() {
        worldTransform = (parent?.worldTransform ?: Matrix4.IDENTITY) * localTransform
    }

    abstract fun updateWorldBounds()

    open fun update(seconds: Float) = true

    open fun render(renderer: Renderer) = true

    fun preUpdate(seconds: Float) = components.forEach { it.preUpdate(seconds, this) }

    fun postUpdate(seconds: Float) = components.forEach { it.postUpdate(seconds, this) }

    fun preRender(frustum: Frustum, renderer: Renderer) = components.forEach { it.preRender(frustum, renderer, this) }

    fun postRender(frustum: Frustum, renderer: Renderer) = components.forEach { it.postRender(frustum, renderer, this) }
}
