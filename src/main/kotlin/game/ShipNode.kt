package game

import engine.graph.BranchNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3
import engine.physics.Particle

class ShipNode : BranchNode() {
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

        return super.update(seconds)
    }
}
