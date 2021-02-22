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

    fun isContainedBy(frustum: Frustum) = frustum.intersects(worldBounds)

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
