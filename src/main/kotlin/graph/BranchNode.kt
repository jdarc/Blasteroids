package graph

import math.Aabb
import math.Matrix4

open class BranchNode(transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {
    private val children = mutableListOf<Node>()

    override val localBounds get() = children.map { it.localBounds }.fold(Aabb(), { dst, src -> dst.aggregate(src); dst })

    fun add(vararg nodes: Node): BranchNode {
        nodes.forEach {
            if (it != this && !children.contains(it)) {
                children.add(it)
                it.setParent(this)
            }
        }
        return this
    }

    fun remove(vararg nodes: Node): BranchNode {
        nodes.forEach {
            if (children.contains(it)) {
                children.remove(it)
                it.setParent(null)
            }
        }
        return this
    }

    override fun traverseDown(visitor: (Node) -> Boolean) {
        if (!visitor(this)) return
        children.forEach { it.traverseDown(visitor) }
    }

    override fun traverseUp(visitor: (Node) -> Unit) {
        children.forEach { it.traverseUp(visitor) }
        visitor(this)
    }

    override fun updateWorldBounds() {
        worldBounds.reset()
        children.forEach { worldBounds.aggregate(it.worldBounds) }
    }
}
