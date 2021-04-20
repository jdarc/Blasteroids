/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package game

import engine.audio.AudioManager
import engine.components.LightSource
import engine.core.*
import engine.graph.BranchNode
import engine.graph.Scene
import engine.math.Scalar
import engine.math.Vector3
import engine.physics.CollisionEvent
import engine.physics.RigidBody
import engine.physics.Simulation
import engine.tools.Scheduler
import game.AsteroidNode.Companion.ASTEROID_CREATED
import game.AsteroidNode.Companion.ASTEROID_DESTROYED
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

@Suppress("SpellCheckingInspection")
class Blasteroids(canvas: HTMLCanvasElement) : Game {
    private val device = Device(canvas)
    private val scheduler = Scheduler()
    private val camera = Camera(fov = Scalar.PI / 8F)
    private val scene = Scene()
    private val events = EventBus()
    private val simulation = Simulation(events, ::filterCollisions)
    private val audioManager = AudioManager()
    private val explodeAudio = audioManager.readSample("sounds", "explode.ogg")
    private val bumpAudio = audioManager.readSample("sounds", "bump.ogg")

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()
        camera.position = Vector3(0F, 0F, 120F)

        val ship = Prefab(Loader.read("models", "fighter.obj"))
        val asteroid1 = Prefab(Loader.read("models", "asteroid1.obj"))
        val asteroid2 = Prefab(Loader.read("models", "asteroid2.obj"))
        val asteroids = arrayOf(asteroid1, asteroid2)

        val arena = BranchNode()
        arena.addComponents(LightSource(0, Color.WHITE, Vector3(0F, 30F, 20F)))

        arena.addNodes(ShipNode(ship, camera, events, simulation, scheduler, audioManager))

        var level = 1
        var asteroidsCount = 0

        events.subscribe(ASTEROID_CREATED) { asteroidsCount += 1 }

        events.subscribe(ASTEROID_DESTROYED) {
            explodeAudio.play()
            asteroidsCount -= 1
            if (asteroidsCount <= 0) {
                level += 1
                spawnAsteroid(level, arena, asteroids)
            }
        }

        events.subscribe(Simulation.COLLISION_EVENT) { impact ->
            impact as CollisionEvent
            if ((impact.body0.data as ObjectTypes == ObjectTypes.SHIP) or
                (impact.body1.data as ObjectTypes == ObjectTypes.SHIP)) {
                bumpAudio.play()
            }
        }

        spawnAsteroid(level, arena, asteroids)

        scene.backcolor = Color(0)
        scene.root.addNodes(arena)
        GameLoop(this).start()
    }

    override fun update(timeStep: Float) {
        scheduler.update(timeStep)
        simulation.integrate(timeStep)
        scene.update(timeStep)
    }

    override fun render() = scene.render(camera, device)

    private fun spawnAsteroid(level: Int, arena: BranchNode, asteroids: Array<Prefab>) {
        scheduler.schedule(0.1F, level / 10F) { arena.addNodes(AsteroidNode(asteroids, camera, events, simulation, 0)) }
    }

    private fun filterCollisions(body0: RigidBody, body1: RigidBody): Boolean {
        val mask = (body0.data as ObjectTypes).mask or (body1.data as ObjectTypes).mask
        return mask != ObjectTypes.SHIP + ObjectTypes.MISSILE
    }
}
