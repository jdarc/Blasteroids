package engine.core

class Textures : MaterialSet<Texture> {
    override var ambient = Texture.DEFAULT
    override var diffuse = Texture.DEFAULT
    override var specular = Texture.DEFAULT
}
