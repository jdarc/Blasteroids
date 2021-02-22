package engine.graph

import engine.core.Material

class MaterialNode(private val material: Material) : BranchNode() {
    override fun render(renderer: Renderer): Boolean {
        renderer.material = material
        return true
    }
}
