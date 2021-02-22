package game

import engine.graph.Geometry
import engine.graph.LeafNode
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.Particle

class MissileNode(geometry: Geometry) : LeafNode(geometry) {
    val particle = Particle()

    override fun update(seconds: Float): Boolean {
        particle.integrate(seconds)
        localTransform = Matrix4.create(particle.position, Matrix4.IDENTITY, Vector3.ONE)
        return super.update(seconds)
    }
}

