/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * GJK-EPA collision solver by Nathanael Presson, 2008.
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

package engine.physics.collision

import engine.math.Scalar
import engine.math.Vector3
import engine.physics.geometry.CollisionSkin
import engine.tools.ObjectPool

class GjkEpaSolver {
    private val gjk = Gjk()
    private val epa = Epa()
    private var results = Results()

    fun collide(shape0: CollisionSkin, shape1: CollisionSkin, margin: Float): Results {
        results.status = ResultsStatus.SEPARATED
        results.gjkIterations = gjk.iterations + 1
        if (gjk.init(shape0, shape1, margin).searchOrigin()) {
            results.epaIterations = epa.iterations + 1
            results.initialPenetration = epa.evaluate(gjk)
            if (results.initialPenetration > 0F) {
                results.r0 = epa.nearest[0]
                results.r1 = epa.nearest[1]
                results.status = ResultsStatus.PENETRATING
                results.normal = epa.normal
            } else if (epa.failed) results.status = ResultsStatus.EPA_FAILED
        } else if (gjk.failed) results.status = ResultsStatus.GJK_FAILED
        return results
    }

    companion object {
        enum class ResultsStatus { SEPARATED, PENETRATING, GJK_FAILED, EPA_FAILED }

        class Results {
            var r0 = Vector3.ZERO
            var r1 = Vector3.ZERO
            var normal = Vector3.ZERO
            var initialPenetration = 0F
            val hasCollided get() = status == ResultsStatus.PENETRATING

            var status = ResultsStatus.SEPARATED
            var epaIterations = 0
            var gjkIterations = 0
        }

        private class He(var v: Vector3 = Vector3.ZERO, var n: He? = null)

        private class Mkv(var w: Vector3 = Vector3.ZERO, var r: Vector3 = Vector3.ZERO) {
            fun set(m: Mkv) = set(m.w, m.r)
            fun set(w: Vector3, r: Vector3) {
                this.w = w
                this.r = r
            }
        }

        private class Face {
            val v = Array(3) { Mkv() }
            val f = arrayOfNulls<Face>(3)
            val e = IntArray(3)
            var n = Vector3.ZERO
            var d = 0F
            var mark = 0
            var prev: Face? = null
            var next: Face? = null

            fun set(a: Mkv, b: Mkv, c: Mkv, epaInFaceEps: Float): Boolean {
                v[0] = a
                v[1] = b
                v[2] = c
                mark = 0
                val nrm = Vector3.cross(b.w - a.w, c.w - a.w)
                n = Vector3.normalize(nrm)
                d = (-Vector3.dot(n, a.w)).coerceIn(Scalar.TINY, Scalar.HUGE)
                return Vector3.crossDot(a.w, b.w, nrm) >= -epaInFaceEps &&
                       Vector3.crossDot(b.w, c.w, nrm) >= -epaInFaceEps &&
                       Vector3.crossDot(c.w, a.w, nrm) >= -epaInFaceEps
            }
        }

        private class Gjk {
            private val hePool = ObjectPool { He() }
            private val table = arrayOfNulls<He>(GJK_HASH_SIZE)

            private lateinit var shape0: CollisionSkin
            private lateinit var shape1: CollisionSkin

            private var margin = 0F
            private var ray = Vector3.ZERO

            val simplex = arrayOf(Mkv(), Mkv(), Mkv(), Mkv(), Mkv())
            var order = 0
            var iterations = 0
            var failed = false

            fun init(shape0: CollisionSkin, shape1: CollisionSkin, margin: Float): Gjk {
                this.shape0 = shape0
                this.shape1 = shape1
                this.margin = margin.coerceAtLeast(Scalar.TINY)
                failed = false
                iterations = 0
                order = -1
                hePool.reset()
                return this
            }

            fun searchOrigin(): Boolean {
                table.fill(null)
                ray = Vector3.UNIT_X
                fetchSupport()
                ray = -simplex[0].w
                while (iterations++ < GJK_MAX_ITERATIONS) {
                    ray = Vector3.normalize(ray)
                    if (fetchSupport()) {
                        if (when (order) {
                                1 -> solveSimplex2(-simplex[1].w, simplex[0].w - simplex[1].w)
                                2 -> solveSimplex3(-simplex[2].w, simplex[1].w - simplex[2].w, simplex[0].w - simplex[2].w)
                                3 -> solveSimplex4(
                                    -simplex[3].w,
                                    simplex[2].w - simplex[3].w,
                                    simplex[1].w - simplex[3].w,
                                    simplex[0].w - simplex[3].w
                                )
                                else -> false
                            }
                        ) return true
                    } else {
                        return false
                    }
                }
                failed = true
                return false
            }

            internal fun support(d: Vector3, v: Mkv): Vector3 {
                v.r = d
                return d * margin + (shape0.getSupport(d) - shape1.getSupport(-d))
            }

            internal fun localSupport(d: Vector3, i: Int) = when (i) {
                0 -> shape0.getSupport(d)
                else -> shape1.getSupport(d)
            }

            private fun fetchSupport(): Boolean {
                val h = hash(ray)
                var e = table[h]
                while (e != null) {
                    e = if (e.v == ray) {
                        order--
                        return false
                    } else e.n
                }
                e = hePool.next()
                e.v = ray
                e.n = table[h]
                table[h] = e
                simplex[++order].w = support(ray, simplex[order])
                return Vector3.dot(ray, simplex[order].w) > 0F
            }

            private fun solveSimplex2(ao: Vector3, ab: Vector3): Boolean {
                when {
                    Vector3.dot(ab, ao) >= 0F -> {
                        val cabo = Vector3.cross(ab, ao)
                        ray = when {
                            cabo.lengthSquared() > GJK_SQ_IN_SIMPLEX_EPS -> Vector3.cross(cabo, ab)
                            else -> return true
                        }
                    }
                    else -> {
                        order = 0
                        simplex[0].set(simplex[1])
                        ray = ao
                    }
                }
                return false
            }

            private fun solveSimplex3(ao: Vector3, ab: Vector3, ac: Vector3) = solveSimplex3a(ao, ab, ac, Vector3.cross(ab, ac))

            private fun solveSimplex3a(ao: Vector3, ab: Vector3, ac: Vector3, cabc: Vector3): Boolean {
                if (Vector3.crossDot(cabc, ab, ao) < -GJK_IN_SIMPLEX_EPS) {
                    order = 1
                    simplex[0].set(simplex[1])
                    simplex[1].set(simplex[2])
                    return solveSimplex2(ao, ab)
                }

                if (Vector3.crossDot(cabc, ac, ao) > GJK_IN_SIMPLEX_EPS) {
                    order = 1
                    simplex[1].set(simplex[2])
                    return solveSimplex2(ao, ac)
                }

                val d = Vector3.dot(cabc, ao)
                if (Scalar.abs(d) > GJK_IN_SIMPLEX_EPS) {
                    if (d > 0F) {
                        ray = cabc
                    } else {
                        ray = -cabc
                        val w = simplex[0].w
                        val r = simplex[0].r
                        simplex[0].set(simplex[1])
                        simplex[1].set(w, r)
                    }
                    return false
                }

                return true
            }

            private fun solveSimplex4(ao: Vector3, ab: Vector3, ac: Vector3, ad: Vector3): Boolean {
                val tmp1 = Vector3.cross(ab, ac)
                if (Vector3.dot(tmp1, ao) > GJK_IN_SIMPLEX_EPS) {
                    order = 2
                    simplex[0].set(simplex[1])
                    simplex[1].set(simplex[2])
                    simplex[2].set(simplex[3])
                    return solveSimplex3a(ao, ab, ac, tmp1)
                }

                val tmp2 = Vector3.cross(ac, ad)
                if (Vector3.dot(tmp2, ao) > GJK_IN_SIMPLEX_EPS) {
                    order = 2
                    simplex[2].set(simplex[3])
                    return solveSimplex3a(ao, ac, ad, tmp2)
                }

                val tmp3 = Vector3.cross(ad, ab)
                if (Vector3.dot(tmp3, ao) > GJK_IN_SIMPLEX_EPS) {
                    order = 2
                    simplex[1].set(simplex[0])
                    simplex[0].set(simplex[2])
                    simplex[2].set(simplex[3])
                    return solveSimplex3a(ao, ad, ab, tmp3)
                }

                return true
            }

            private companion object {
                const val GJK_MAX_ITERATIONS = 128
                const val GJK_HASH_SIZE = 64
                const val GJK_HASH_MASK = GJK_HASH_SIZE - 1
                const val GJK_IN_SIMPLEX_EPS = 0.0001F
                const val GJK_SQ_IN_SIMPLEX_EPS = GJK_IN_SIMPLEX_EPS * GJK_IN_SIMPLEX_EPS

                fun hash(v: Vector3): Int {
                    val hx = (v.x * 15461).toInt()
                    val hy = (v.y * 83003).toInt()
                    val hz = (v.z * 15473).toInt()
                    return GJK_HASH_MASK and (hx xor hy xor hz) * 169639
                }
            }
        }

        private class Epa {
            private val mkvPool = ObjectPool { Mkv() }
            private val facePool = ObjectPool { Face() }
            private var root: Face? = null
            private var faceCount = 0
            private var depth = 0F
            private val baseMkv = Array(4) { Mkv() }
            private val baseFaces = Array(6) { Face() }
            private var pfIdxPtr = emptyArray<IntArray>()
            private var peIdxPtr = emptyArray<IntArray>()
            private val cf = arrayOf<Face?>(null)
            private val ff = arrayOf<Face?>(null)

            val nearest = arrayOf(Vector3.ZERO, Vector3.ZERO)
            var normal = Vector3.ZERO
            var iterations = 0
            var failed = false

            fun evaluate(gjk: Gjk, accuracy: Float = EPA_ACCURACY): Float {
                if (gjk.order !in 3..4) return depth
                mkvPool.reset()
                facePool.reset()

                root = null
                depth = -INF
                normal = Vector3.ZERO
                failed = false
                faceCount = 0
                iterations = 0

                val nfIdx: Int
                val neIdx: Int
                if (gjk.order == 3) {
                    pfIdxPtr = TETRAHEDRON_FIDX
                    peIdxPtr = TETRAHEDRON_EIDX
                    nfIdx = 4
                    neIdx = 6
                } else {
                    pfIdxPtr = HEXAHEDRON_FIDX
                    peIdxPtr = HEXAHEDRON_EIDX
                    nfIdx = 6
                    neIdx = 9
                }

                (0..gjk.order).forEach { baseMkv[it].set(gjk.simplex[it]) }

                repeat((0 until nfIdx).count()) {
                    newFace(baseFaces[it], baseMkv[pfIdxPtr[it][0]], baseMkv[pfIdxPtr[it][1]], baseMkv[pfIdxPtr[it][2]])
                }

                repeat((0 until neIdx).count()) {
                    link(baseFaces[peIdxPtr[it][0]], peIdxPtr[it][1], baseFaces[peIdxPtr[it][2]], peIdxPtr[it][3])
                }

                if (faceCount == 0) return depth

                var markId = 1
                var bestFace: Face? = null
                while (iterations++ < EPA_MAX_ITERATIONS) {
                    val bf = findBest(root) ?: break
                    val mkv = mkvPool.next()
                    mkv.w = gjk.support(-bf.n, mkv)
                    bestFace = bf
                    if (Vector3.dot(bf.n, mkv.w) + bf.d >= -accuracy) break
                    detach(bf)
                    bf.mark = ++markId
                    cf[0] = null
                    ff[0] = null
                    if (buildHorizon(markId, mkv, bf.f[0]!!, bf.e[0], cf, ff) +
                        buildHorizon(markId, mkv, bf.f[1]!!, bf.e[1], cf, ff) +
                        buildHorizon(markId, mkv, bf.f[2]!!, bf.e[2], cf, ff) < 3
                    ) break
                    link(cf[0]!!, 1, ff[0]!!, 2)
                }

                if (bestFace == null) {
                    failed = true
                } else {
                    normal = bestFace.n
                    depth = bestFace.d.coerceAtLeast(0F)
                    val features00 = gjk.localSupport(bestFace.v[0].r, 0)
                    val features01 = gjk.localSupport(bestFace.v[1].r, 0)
                    val features02 = gjk.localSupport(bestFace.v[2].r, 0)
                    val features10 = gjk.localSupport(-bestFace.v[0].r, 1)
                    val features11 = gjk.localSupport(-bestFace.v[1].r, 1)
                    val features12 = gjk.localSupport(-bestFace.v[2].r, 1)
                    val w0 = bestFace.v[0].w + bestFace.n * bestFace.d
                    val w1 = bestFace.v[1].w + bestFace.n * bestFace.d
                    val w2 = bestFace.v[2].w + bestFace.n * bestFace.d
                    val x = Vector3.crossLength(w0, w1)
                    val y = Vector3.crossLength(w1, w2)
                    val z = Vector3.crossLength(w2, w0)
                    val dn = 1F / (x + y + z).coerceAtLeast(Scalar.TINY)
                    nearest[0] = features00 * (y * dn) + features01 * (z * dn) + features02 * (x * dn)
                    nearest[1] = features10 * (y * dn) + features11 * (z * dn) + features12 * (x * dn)
                }
                return depth
            }

            private fun newFace(pf: Face, a: Mkv, b: Mkv, c: Mkv): Face {
                if (pf.set(a, b, c, EPA_IN_FACE_EPS)) {
                    if (root != null) root!!.prev = pf
                    pf.prev = null
                    pf.next = root
                    root = pf
                    faceCount++
                } else {
                    pf.next = null
                    pf.prev = pf.next
                }
                return pf
            }

            private fun detach(face: Face) {
                if (face.prev == null && face.next == null) return
                faceCount--
                when (face) {
                    root -> {
                        root = face.next
                        root?.prev = null
                    }
                    else -> when (face.next) {
                        null -> face.prev?.next = null
                        else -> {
                            face.prev?.next = face.next
                            face.next?.prev = face.prev
                        }
                    }
                }
                face.next = null
                face.prev = face.next
            }

            private fun buildHorizon(markId: Int, w: Mkv, f: Face, e: Int, cf: Array<Face?>, ff: Array<Face?>): Int {
                var ne = 0
                if (f.mark != markId) {
                    val e1 = MOD3[e + 1]
                    if (Vector3.dot(f.n, w.w) + f.d > 0F) {
                        val nf = newFace(facePool.next(), f.v[e1], f.v[e], w)
                        link(nf, 0, f, e)
                        if (cf[0] != null) {
                            link(cf[0]!!, 1, nf, 2)
                        } else {
                            ff[0] = nf
                        }
                        cf[0] = nf
                        ne = 1
                    } else {
                        val e2 = MOD3[e + 2]
                        detach(f)
                        f.mark = markId
                        ne += buildHorizon(markId, w, f.f[e1]!!, f.e[e1], cf, ff)
                        ne += buildHorizon(markId, w, f.f[e2]!!, f.e[e2], cf, ff)
                    }
                }
                return ne
            }

            private companion object {
                const val INF = Float.MAX_VALUE
                const val EPA_MAX_ITERATIONS = 256
                const val EPA_IN_FACE_EPS = 0.01F
                const val EPA_ACCURACY = 0.001F

                fun findBest(root: Face?): Face? {
                    if (root == null) return null
                    var cf = root
                    var bd = INF
                    var bf: Face? = null
                    do {
                        if (cf!!.d < bd) {
                            bd = cf.d
                            bf = cf
                        }
                        cf = cf.next
                    } while (cf != null)
                    return bf
                }

                fun link(f0: Face, e0: Int, f1: Face, e1: Int) {
                    f0.f[e0] = f1
                    f1.e[e1] = e0
                    f1.f[e1] = f0
                    f0.e[e0] = e1
                }

                val MOD3 = intArrayOf(0, 1, 2, 0, 1)

                val TETRAHEDRON_FIDX = arrayOf(
                    intArrayOf(2, 1, 0),
                    intArrayOf(3, 0, 1),
                    intArrayOf(3, 1, 2),
                    intArrayOf(3, 2, 0)
                )

                val TETRAHEDRON_EIDX = arrayOf(
                    intArrayOf(0, 0, 2, 1),
                    intArrayOf(0, 1, 1, 1),
                    intArrayOf(0, 2, 3, 1),
                    intArrayOf(1, 0, 3, 2),
                    intArrayOf(2, 0, 1, 2),
                    intArrayOf(3, 0, 2, 2)
                )

                val HEXAHEDRON_FIDX = arrayOf(
                    intArrayOf(2, 0, 4),
                    intArrayOf(4, 1, 2),
                    intArrayOf(1, 4, 0),
                    intArrayOf(0, 3, 1),
                    intArrayOf(0, 2, 3),
                    intArrayOf(1, 3, 2)
                )

                val HEXAHEDRON_EIDX = arrayOf(
                    intArrayOf(0, 0, 4, 0),
                    intArrayOf(0, 1, 2, 1),
                    intArrayOf(0, 2, 1, 2),
                    intArrayOf(1, 1, 5, 2),
                    intArrayOf(1, 0, 2, 0),
                    intArrayOf(2, 2, 3, 2),
                    intArrayOf(3, 1, 5, 0),
                    intArrayOf(3, 0, 4, 2),
                    intArrayOf(5, 1, 4, 1)
                )
            }
        }
    }
}
