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
import engine.tools.Subscriber
import kotlin.random.Random

class AsteroidNode(private val simulation: Simulation, asteroids: Array<Geometry>) : BranchNode(), Subscriber<CollisionInfo> {
    private val axis = Vector3.random()
    private val size = 6F + 0.5F * Random.nextFloat()
    private var angle = 0F
    private val speed = 1F + Random.nextFloat() * 6F / size
    private val body = RigidBody()

    override fun update(seconds: Float): Boolean {
        angle += speed * seconds
        transform = Matrix4.create(body.position, Matrix4.createFromAxisAngle(axis, angle), Vector3(size))
        return super.update(seconds)
    }

    init {
        transform = Matrix4.createTranslation(Vector3.random(1F, 1F, 0F) * 30F)
        addNodes(LeafNode(asteroids[rnd.nextInt(0, asteroids.size)]))
        addComponents(Physics(body), WrapAround { body.position = it })
        body.data = "Asteroid"
        body.boundingSphere = size
        simulation.addBody(body)
        simulation.subscribe("Collision", this)
        body.position = Vector3.random(1F, 1F, 0F) * 30F
        body.velocity = Vector3.random(1F, 1F, 0F) * Random.nextFloat() * (10F - size)
    }

    override fun update(context: CollisionInfo) {
        if (context.involves(body) && context.hasData("Missile")) {
            simulation.unsubscribe("Collision", this)
            simulation.removeBody(body)
            parent?.removeNodes(this)
        }
    }

    companion object {
        private val rnd = Random(1973)
    }
}
