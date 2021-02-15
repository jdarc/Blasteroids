package core

class Material(val name: String) {
    val colors = Colors()
    val textures = Textures()
    var shininess = 30F

    companion object {
        val DEFAULT = Material("")
    }
}
