package game

import engine.core.Color
import engine.core.Material
import engine.core.Primitives
import engine.graph.BranchNode
import engine.graph.MaterialNode
import engine.io.Keyboard
import engine.io.Keys
import engine.math.Matrix4
import engine.math.Vector3

class GunNode(transform: Matrix4) : BranchNode(transform) {
    private var fireRate = 0.2F
    private var speed = 50F
    private var timer = 0F
    private var last = 0F

    override fun update(seconds: Float): Boolean {
        timer += seconds
        if (Keyboard.getState().isKeyDown(Keys.S)) fire()
        return super.update(seconds)
    }

    private fun fire() {
        if (timer - last < fireRate) return
        root?.add(spawnMissile())
        last = timer
    }

    private fun spawnMissile(): BranchNode {
        val position = Vector3(worldTransform.m03, worldTransform.m13, worldTransform.m23)
        val velocity = Vector3(-worldTransform.m10, worldTransform.m00, 0F) * speed
        return MaterialNode(MISSILE_MATERIAL).add(MissileNode(MISSILE).apply {
            particle.position = position
            particle.velocity = velocity
        })
    }

    private companion object {
        val MISSILE_MATERIAL = Material("missile").apply {
            colors.ambient = Color.WHITE
            colors.diffuse = Color.WHITE
        }
        val MISSILE = Primitives.createSphere(0.2F, 8, 8)
    }
}
