package game

import engine.graph.BranchNode
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.Particle
import kotlin.random.Random

class AsteroidNode : BranchNode() {
    private val particle = Particle().apply {
        position = random(1F, 1F, 0F) * 20F
        velocity = random(1F, 1F, 0F)
    }
    private var size = 2F + Random.nextFloat() * 8F
    private var yaw = Random.nextDouble(-3.0, 3.0).toFloat()
    private var roll = Random.nextDouble(-3.0, 3.0).toFloat()
    private var speed = (Random.nextDouble() + 0.1).toFloat() / 30F
    private var speed2 = (Random.nextDouble() + 0.1).toFloat() / 30F

    override fun update(seconds: Float): Boolean {
        particle.integrate(seconds)
        roll += speed
        yaw += speed2
        val rotation = Matrix4.createFromYawPitchRoll(yaw, 0F, roll)
        localTransform = Matrix4.create(particle.position, rotation, Vector3(size))
        return super.update(seconds)
    }

    companion object {
        private fun random(x: Float = 1F, y: Float = 1F, z: Float = 1F): Vector3 {
            val vx = Random.nextDouble(-1.0, 1.0).toFloat() * x
            val vy = Random.nextDouble(-1.0, 1.0).toFloat() * y
            val vz = Random.nextDouble(-1.0, 1.0).toFloat() * z
            return Vector3.normalize(Vector3(vx, vy, vz))
        }
    }
}
