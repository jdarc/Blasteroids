package game

import engine.components.Physics
import engine.components.WrapAround
import engine.graph.BranchNode
import engine.graph.Geometry
import engine.graph.LeafNode
import engine.math.Matrix4
import engine.math.Vector3
import engine.physics.CollisionInfo
import engine.physics.RigidBody
import engine.physics.Simulation
import engine.physics.Simulation.Companion.COLLISION_EVENT
import engine.tools.Subscriber
import kotlin.random.Random

class AsteroidNode(
    private val events: EventBus,
    private val simulation: Simulation,
    private val level: Int = 0,
    private val asteroids: Array<Geometry>
) : BranchNode(), Subscriber<Any> {
    private var energy = 8 shr level
    private val axis = Vector3.random()
    private val size = (6F + 0.5F * Random.nextFloat()) / (1 shl level).toFloat()
    private var angle = 0F
    private val speed = 1F + Random.nextFloat() * 8F / size
    private val body = RigidBody()

    override fun update(seconds: Float): Boolean {
        angle += speed * seconds
        transform = Matrix4.create(body.position, Matrix4.createFromAxisAngle(axis, angle), Vector3(size))
        return super.update(seconds)
    }

    override fun notify(context: Any) {
        if (context is CollisionInfo && context.involves(body) && context.hasData(ObjectTypes.MISSILE)) {
            energy -= 1
            if (energy <= 0) {
                events.unsubscribe(COLLISION_EVENT, this)
                simulation.removeBody(body)

                if (level < 2) {
                    for (i in 0 until 4.shr(level)) {
                        val asteroid = AsteroidNode(events, simulation, level + 1, asteroids)
                        asteroid.body.position = body.position
                        parent?.addNodes(asteroid)
                    }
                }
                parent?.removeNodes(this)
                events.notify(ASTEROID_DESTROYED, this)
            }
        }
    }

    init {
        transform = Matrix4.createTranslation(Vector3.random(1F, 1F, 0F) * 30F)
        body.data = ObjectTypes.ASTEROID
        body.boundingSphere = size
        body.position = Vector3.random(1F, 1F, 0F) * 30F
        body.velocity = Vector3.random(1F, 1F, 0F) * Random.nextFloat() * 5F * speed
        simulation.addBody(body)

        addNodes(LeafNode(asteroids[rnd.nextInt(0, asteroids.size)]))
        addComponents(Physics(body), WrapAround { body.position = it })

        events.subscribe(COLLISION_EVENT, this)
        events.notify(ASTEROID_CREATED, this)
    }

    companion object {
        const val ASTEROID_CREATED = "asteroid.created"
        const val ASTEROID_DESTROYED = "asteroid.destroyed"

        private val rnd = Random(1973)
    }
}
