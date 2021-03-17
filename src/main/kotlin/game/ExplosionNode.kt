package game

import engine.components.MaterialOverride
import engine.components.Physics
import engine.core.Color
import engine.core.Material
import engine.core.Mesh
import engine.core.Primitives
import engine.graph.BranchNode
import engine.graph.LeafNode
import engine.physics.RigidBody

class ExplosionNode : BranchNode() {
    private val body = RigidBody()
    private var blastTime = 1.2F
    private var colorChangeTime = 0.15F
    private var explosionRadius = 0F
    private var redColor = true
    private var explosionMesh = Mesh(floatArrayOf(0F), 0)


    override fun update(seconds: Float): Boolean {
        if (blastTime < 0F) destroy()
        if (colorChangeTime < 0F) changeColor()

        blastTime -= seconds
        colorChangeTime -= seconds

        explosionRadius *= 0.95F
        explosionMesh = getExplosionGeometry(explosionRadius)

        removePreviousExplosion()

        addNewExplosion()

        return super.update(seconds)
    }

    private fun removePreviousExplosion() {
        this.clearChildren()
        this.removeAllComponents()
    }

    private fun changeColor() {
        redColor = !redColor
        colorChangeTime = 0.15F
    }

    private fun destroy() {
        parent?.removeNodes(this)
    }

    init {
        body.data = ObjectTypes.EXPLOSION
    }

    fun explode(explodee: RigidBody) {
        explosionRadius = explodee.boundingSphere
        explosionMesh = getExplosionGeometry(explosionRadius)
        addNewExplosion()
        body.position = explodee.position
    }

    private fun addNewExplosion() {
        addNodes(LeafNode(explosionMesh))
        addComponents(MaterialOverride(getExplosionMaterial()), Physics(body))
    }

    private fun getExplosionGeometry(radius: Float) = Primitives.createSphere(radius, 8, 8)

    private fun getExplosionMaterial(): Material {
        return Material().apply {
            colors.diffuse = getColor()
            colors.specular = getColor()
            shininess = 50F
        }
    }

    private fun getColor() : Color {
        return if (!redColor) {
            Color.YELLOW
        } else Color.RED
    }

}