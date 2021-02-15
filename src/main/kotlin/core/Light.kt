package core

import math.Vector3

interface Light {
    var position: Vector3
    var color: Color
    var on: Boolean
}
