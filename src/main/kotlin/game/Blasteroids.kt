package game

import engine.core.*
import engine.graph.LeafNode
import engine.graph.LightNode
import engine.graph.Scene
import engine.math.Matrix4
import engine.math.Scalar
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
    private val scheduler = Scheduler()
    private val camera = Camera(fov = Scalar.PI / 8F)
    private var scene = Scene()

    suspend fun run() {
        window.addEventListener("webglcontextlost", { GlobalScope.launch { device.initialize() } })
        device.initialize()
        camera.position = Vector3(0F, 0F, 120F)
        scene.backcolor = Color(0)
        scene.root.add(
            LightNode(0, Color.WHITE).moveTo(Vector3(0F, 0F, 500F)),
            ShipNode(camera).add(
                GunNode(scheduler, Matrix4.createTranslation(-1.7F, 0.9F, 0F)),
                GunNode(scheduler, Matrix4.createTranslation(1.7F, 0.9F, 0F)),
                LeafNode(Loader.read("models", "fighter.obj"))
            )
        )

        GameLoop(this).start()
    }

    override fun update(timeStep: Float) {
        scheduler.update(timeStep)
        scene.update(timeStep)
    }

    override fun render() = scene.render(camera, device)
}
