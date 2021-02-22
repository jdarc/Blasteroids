package game

import engine.core.Camera
import engine.graph.BranchNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.*
import engine.physics.Particle

class ShipNode(private val camera: Camera) : BranchNode() {
    private var angularSpeed = Scalar.PI
    private var thrustSpeed = 20F
    private var radians = 0F
    private val particle = Particle().apply { dampingFactor = 0.5F }

    override fun update(seconds: Float): Boolean {
        val keyboard = Keyboard.getState()

        when {
            keyboard.isKeyDown(Keys.Left) -> radians += angularSpeed * seconds
            keyboard.isKeyDown(Keys.Right) -> radians -= angularSpeed * seconds
        }
        val rotation = Matrix4.createRotationZ(radians)

        particle.clearForces()
        if (keyboard.isKeyDown(Keys.Up)) {
            particle.addForce(rotation * Vector3(0F, thrustSpeed, 0F))
        }
        particle.integrate(seconds)

        localTransform = Matrix4.create(particle.position, rotation, Vector3.ONE)

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
