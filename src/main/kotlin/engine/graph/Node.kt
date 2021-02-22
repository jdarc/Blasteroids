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
    var localTransform = transform
    var worldTransform = Matrix4.IDENTITY

    abstract val localBounds: Aabb
    val worldBounds = Aabb()

    fun isContainedBy(frustum: Frustum) = frustum.contains(worldBounds)

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

    open fun traverseDown(visitor: (Node) -> Boolean) {
        visitor(this)
    }

    open fun traverseUp(visitor: (Node) -> Unit) {
        visitor(this)
    }

    open fun updateTransform() {
        worldTransform = (parent?.worldTransform ?: Matrix4.IDENTITY) * localTransform
    }

    abstract fun updateWorldBounds()

    open fun update(seconds: Float) = true

    open fun render(renderer: Renderer) = true

    fun moveTo(position: Vector3): Node {
        val local = localTransform
        localTransform = Matrix4(
            local.m00, local.m10, local.m20, local.m30,
            local.m01, local.m11, local.m21, local.m31,
            local.m02, local.m12, local.m22, local.m32,
            position.x, position.y, position.z, local.m33
        )
        return this
    }
}
