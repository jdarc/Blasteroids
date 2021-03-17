package engine.physics

import engine.graph.BranchNode
import engine.tools.Subscriber
import game.EventBus
import game.ExplosionNode

class Exploder(private val events: EventBus, private val arena: BranchNode) : Subscriber<Any> {

    init {
        events.subscribe(SHIP_DESTROYED_EVENT, this)
        events.subscribe(ASTEROID_DESTROYED, this)
    }

    fun createExplosion(explodee: RigidBody) {
        val explosion = ExplosionNode()
        explosion.explode(explodee)
        arena.addNodes(explosion)
    }

    override fun notify(context: Any) {
        if (context is DestructionInfo) {
            createExplosion(context.getObject())
        }
    }

    companion object {
        const val SHIP_DESTROYED_EVENT = "ship.destroyed"
        const val ASTEROID_DESTROYED = "asteroid.destroyed"
    }

}