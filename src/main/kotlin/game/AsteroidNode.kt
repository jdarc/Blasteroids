package game

import engine.components.Physics
import engine.components.WrapAround
import engine.core.Camera
import engine.graph.BranchNode
import engine.graph.LeafNode
import engine.math.Scalar
import engine.math.Scalar.sqr
import engine.math.Vector3
import engine.physics.CollisionEvent
import engine.physics.RigidBody
import engine.physics.Simulation
import engine.physics.Simulation.Companion.COLLISION_EVENT
import engine.tools.Subscriber

class AsteroidNode(
    private val prefabs: Array<Prefab>,
    private val camera: Camera,
    private val events: EventBus,
    private val simulation: Simulation,
    private val level: Int = 0
) : BranchNode(), Subscriber<Any> {
    private var energy = (3.shr(level) + 2).sqr()
    private val size = (6F + 0.5F * Scalar.rnd()) / (1 shl level).toFloat()
    private val speed = 1F + Scalar.rnd() * 8F / size
    private val body: RigidBody
    private val lockZAxis: LockZAxis

    override fun notify(context: Any) {
        if (missileImpact(context)) {
            energy -= 1
            if (energy <= 0) {
                events.unsubscribe(COLLISION_EVENT, this)
                simulation.removeBody(body)
                simulation.constraints -= lockZAxis

                spawnFragments()

                parent?.removeNodes(this)
                events.notify(ASTEROID_DESTROYED, this)
            }
        }
    }

    private fun missileImpact(context: Any): Boolean {
        return context is CollisionEvent && context.involves(body) && context.hasData(ObjectTypes.MISSILE)
    }

    private fun spawnFragments() {
        if (level >= 2) return
        for (i in 0 until 3.shl(level)) {
            val asteroid = AsteroidNode(prefabs, camera, events, simulation, level + 1)
            asteroid.body.position = body.position + Vector3.normalize(asteroid.body.linearVelocity) * size * 0.3F
            parent?.addNodes(asteroid)
        }
    }

    init {
        events.subscribe(COLLISION_EVENT, this)
        events.notify(ASTEROID_CREATED, this)

        val prefab = prefabs[Scalar.rnd(0, prefabs.size)]

        body = RigidBody(prefab.generateHull(size))
        body.skin.friction = 0F
        body.skin.restitution = 1F
        body.mass = 1000F * size
        body.data = ObjectTypes.ASTEROID
        body.position = Vector3.random(1F, 1F, 0F) * 30F
        body.linearVelocity = Vector3.random() * Scalar.rnd() * speed * 5F
        body.angularVelocity = Vector3.random() * Scalar.rnd() * speed
        lockZAxis = LockZAxis(body)

        simulation.addBody(body)
        simulation.constraints += lockZAxis

        addNodes(LeafNode(prefab.geometry))
        addComponents(Physics(body, size), WrapAround(camera) { body.position = it })
    }

    companion object {
        const val ASTEROID_CREATED = "asteroid.created"
        const val ASTEROID_DESTROYED = "asteroid.destroyed"
    }
}
