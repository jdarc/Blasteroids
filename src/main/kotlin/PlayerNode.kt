@file:Suppress("MemberVisibilityCanBePrivate")

import graph.BranchNode
import io.Keyboard
import io.Keys
import math.Matrix4
import math.Scalar
import math.Vector3
import physics.Particle

class PlayerNode : BranchNode() {
    private var angularSpeed = Scalar.PI
    private var thrustSpeed = 20F
    private var radians = 0F
    private val particle = Particle()

    override fun update(seconds: Float): Boolean {
        val keyboard = Keyboard.getState()

        if (keyboard.isKeyDown(Keys.Left)) {
            radians += angularSpeed * seconds
        } else if (keyboard.isKeyDown(Keys.Right)) {
            radians -= angularSpeed * seconds
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
