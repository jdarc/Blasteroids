@file:Suppress("SpellCheckingInspection")

import core.*
import graph.LeafNode
import graph.LightNode
import graph.Scene
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import math.Frustum
import math.Vector3
import org.w3c.dom.HTMLCanvasElement
import webgl.WebGL2RenderingContext

suspend fun main() {
    document.body?.addEventListener("contextmenu", { it.preventDefault() })
    Blasteroids(document.querySelector("canvas") as HTMLCanvasElement).run()
}

class Blasteroids(canvas: HTMLCanvasElement) : Game {
    private val gl = canvas.getContext("webgl2") as WebGL2RenderingContext
    private val device = Device(gl)
    private val camera = Camera()
    private var scene = Scene()

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()

        camera.position = Vector3(0F, 0F, 50F)

        scene.backcolor = Color.create(0, 0)
        scene.root.add(
            LightNode(0, Color.WHITE).moveTo(Vector3(0F, 0F, 100F)),
            PlayerNode().add(LeafNode(Loader.read("models", "fighter.obj")))
        )

        GameLoop(this).start()
    }

    override fun update(timeStep: Float) = scene.update(timeStep)

    override fun render() {
        if (!device.isReady) throw RuntimeException("device is not ready")
        device.resize()
        camera.aspectRatio = device.aspectRatio
        scene.render(Frustum(camera.view, camera.projection), device)
    }
}
