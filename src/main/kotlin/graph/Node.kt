@file:Suppress("MemberVisibilityCanBePrivate")

package graph

import math.Aabb
import math.Frustum
import math.Matrix4
import math.Vector3

abstract class Node(transform: Matrix4 = Matrix4.IDENTITY) {
    private var parent: BranchNode? = null

    protected var worldTransform = Matrix4.IDENTITY
    var localTransform = transform

    val worldBounds = Aabb()
    abstract val localBounds: Aabb
    abstract fun updateWorldBounds()

    fun isContainedBy(frustum: Frustum) = frustum.intersects(worldBounds)

    fun isParent(node: BranchNode?) = node == parent

    fun setParent(node: BranchNode?) {
        if (isParent(node)) return
        parent?.remove(this)
        parent = node
        parent?.add(this)
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
