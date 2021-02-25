package game

import engine.core.Camera
import engine.graph.BranchNode
import engine.math.Frustum
import engine.math.Matrix4
import engine.math.Ray
import engine.math.Vector3
import engine.physics.Particle
import kotlin.random.Random

class AsteroidNode(private val camera: Camera) : BranchNode() {
    private val axis = Vector3.random()
    private val size = 1F + 5F * Random.nextFloat()
    private var angle = 0F
    private val speed = 1F + Random.nextFloat() * 6F / size
    private val particle = Particle().apply {
        position = Vector3.random(1F, 1F, 0F) * 30F
        velocity = Vector3.random(1F, 1F, 0F) * Random.nextFloat() * (10F - size)
    }

    override fun update(seconds: Float): Boolean {
        particle.integrate(seconds)
        angle += speed * seconds
        val rotation = Matrix4.createFromAxisAngle(axis, angle)
        localTransform = Matrix4.create(particle.position, rotation, Vector3(size))
        wrapAround()
        return super.update(seconds)
    }

    private fun wrapAround() {
        val frustum = Frustum(camera)
        if (!frustum.contains(worldBounds)) {
            val top = frustum.intersect(Ray(Vector3.ZERO, Vector3.UNIT_Y))
            val right = frustum.intersect(Ray(Vector3.ZERO, Vector3.UNIT_X))
            val bot = frustum.intersect(Ray(Vector3.ZERO, -Vector3.UNIT_Y))
            val left = frustum.intersect(Ray(Vector3.ZERO, -Vector3.UNIT_X))
            var (x, y, z) = particle.position
            if (x < left.x) x = right.x else if (x > right.x) x = left.x
            if (y < bot.y) y = top.y else if (y > top.y) y = bot.y
            particle.position = Vector3(x, y, z)
        }
    }
}
