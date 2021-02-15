@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package graph

class SwitchNode(initialValue: Boolean = true) : BranchNode() {
    var on = initialValue

    override fun update(seconds: Float) = on

    override fun render(renderer: Renderer) = on
}
