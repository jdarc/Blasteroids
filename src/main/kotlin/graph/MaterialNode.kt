@file:Suppress("unused")

package graph

import core.Material

class MaterialNode(private val material: Material) : BranchNode() {
    override fun render(renderer: Renderer): Boolean {
        renderer.material = material
        return true
    }
}
