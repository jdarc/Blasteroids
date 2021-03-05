package game

import engine.graph.BranchNode
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.Particle
import kotlin.random.Random

class AsteroidNode : BranchNode() {
    private val axis = Vector3.random()
    private val size = 6F + 0.5F * Random.nextFloat()
    private var angle = 0F
    private val speed = 1F + Random.nextFloat() * 6F / size
    private val particle = Particle().apply {
        position = Vector3.random(1F, 1F, 0F) * 30F
        velocity = Vector3.random(1F, 1F, 0F) * Random.nextFloat() * (10F - size)
    }

    override fun update(seconds: Float): Boolean {
        particle.position = position
        particle.integrate(seconds)
        angle += speed * seconds
        localTransform = Matrix4.create(particle.position, Matrix4.createFromAxisAngle(axis, angle), Vector3(size))
        return super.update(seconds)
    }

    init {
        localTransform = Matrix4.createTranslation(Vector3.random(1F, 1F, 0F) * 30F)
    }
}
