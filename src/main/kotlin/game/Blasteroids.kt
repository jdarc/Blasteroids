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

import engine.components.LightSource
import engine.core.*
import engine.graph.BranchNode
import engine.graph.Geometry
import engine.graph.Scene
import engine.math.Scalar
import engine.math.Vector3
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
    private val simulation = Simulation(events)

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()
        camera.position = Vector3(0F, 0F, 120F)

        val shipGeometry = Loader.read("models", "fighter.obj")
        val asteroid1Geometry = Loader.read("models", "asteroid1.obj")
        val asteroid2Geometry = Loader.read("models", "asteroid2.obj")
        val asteroids = arrayOf(asteroid1Geometry, asteroid2Geometry)

        val arena = BranchNode()
        arena.addComponents(LightSource(0, Color.WHITE, Vector3(0F, 30F, 20F)))

        arena.addNodes(ShipNode(shipGeometry, camera, events, simulation, scheduler))

        var level = 1
        var asteroidsCount = 0

        events.subscribe(ASTEROID_CREATED) { asteroidsCount += 1 }
        events.subscribe(ASTEROID_DESTROYED) {
            asteroidsCount -= 1
            if (asteroidsCount <= 0) {
                level += 1
                spawnAsteroid(level, arena, asteroids)
            }
        }

        spawnAsteroid(level, arena, asteroids)

        scene.backcolor = Color(0)
        scene.root.addNodes(arena)
        GameLoop(this).start()
    }

    private fun spawnAsteroid(level: Int, arena: BranchNode, asteroids: Array<Geometry>) {
        scheduler.schedule(0.1F, (level + 4) / 10F) { arena.addNodes(AsteroidNode(asteroids, camera, events, simulation, 0)) }
    }

    override fun update(timeStep: Float) {
        scheduler.update(timeStep)
        simulation.update(timeStep)
        scene.update(timeStep)
    }

    override fun render() = scene.render(camera, device)
}
