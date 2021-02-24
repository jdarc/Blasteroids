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

import engine.core.*
import engine.graph.LeafNode
import engine.graph.LightNode
import engine.graph.Scene
import engine.math.Matrix4
import engine.math.Scalar
import engine.math.Vector3
import engine.tools.Scheduler
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.random.Random

@Suppress("SpellCheckingInspection")
class Blasteroids(canvas: HTMLCanvasElement) : Game {
    private val device = Device(canvas)
    private val scheduler = Scheduler()
    private val camera = Camera(fov = Scalar.PI / 8F)
    private var scene = Scene()

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()
        camera.position = Vector3(0F, 0F, 120F)
        scene.backcolor = Color(0)
        scene.root.add(
            LightNode(0, Color.WHITE).moveTo(Vector3(0F, 0F, 30F)),
            ShipNode(camera).add(
                GunNode(scheduler, Matrix4.createTranslation(-1.7F, 0.9F, 0F)),
                GunNode(scheduler, Matrix4.createTranslation(1.7F, 0.9F, 0F)),
                LeafNode(Loader.read("models", "fighter.obj"))
            )
        )

        val rnd = Random(1973)
        val asteroid = Loader.read("models", "asteroid.obj")
        val asteroid1 = Loader.read("models", "asteroid1.obj")
        val asteroid2 = Loader.read("models", "asteroid2.obj")
        val asteroids = arrayOf(asteroid, asteroid1, asteroid2)
        scheduler.schedule(1F, 10F) {
            scene.root.add(AsteroidNode(camera).add(LeafNode(asteroids[rnd.nextInt(0, asteroids.size)])))
        }
        GameLoop(this).start()
    }

    override fun update(timeStep: Float) {
        scheduler.update(timeStep)
        scene.update(timeStep)
    }

    override fun render() = scene.render(camera, device)
}
