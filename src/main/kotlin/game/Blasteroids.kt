package game

import engine.core.*
import engine.graph.LeafNode
import engine.graph.LightNode
import engine.graph.Scene
import engine.math.Frustum
import engine.math.Matrix4
import engine.math.Vector3
import engine.webgl.WebGL2RenderingContext
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

@Suppress("SpellCheckingInspection")
class Blasteroids(canvas: HTMLCanvasElement) : Game {
    private val gl = canvas.getContext("webgl2") as WebGL2RenderingContext
    private val device = Device(gl)
    private val camera = Camera()
    private var scene = Scene()

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()

        camera.position = Vector3(0F, 0F, 50F)

        scene.backcolor = Color(0)
        scene.root.add(
            LightNode(0, Color.WHITE).moveTo(Vector3(0F, 0F, 100F)),
            ShipNode().add(
                GunNode(Matrix4.createTranslation(-1.7F, 0.9F, 0F)),
                GunNode(Matrix4.createTranslation(1.7F, 0.9F, 0F)),
                LeafNode(Loader.read("models", "fighter.obj"))
            )
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
