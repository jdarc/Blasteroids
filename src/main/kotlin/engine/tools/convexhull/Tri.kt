/*
 * Copyright (c) 2008 Stan Melax http://www.melax.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package engine.tools.convexhull

internal class Tri(a: Int, b: Int, c: Int) : Int3(a, b, c) {
    var n = Int3(-1, -1, -1)
    var id = 0
    var vmax = -1
    var rise = 0f

    fun neib(a: Int, b: Int): IntRef {
        for (i in 0..2) {
            if (this[i] == a && this[(i + 1) % 3] == b) return n.getRef((i + 2) % 3)
            if (this[i] == b && this[(i + 1) % 3] == a) return n.getRef((i + 2) % 3)
        }
        return erRef
    }

    companion object {
        private val erRef = object : IntRef {
            private var er = -1
            override fun get() = er
            override fun set(value: Int) {
                er = value
            }
        }
    }
}
