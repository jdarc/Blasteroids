@file:Suppress("unused")

package io

class KeyBoardState(private val state: BooleanArray) {
    fun isKeyDown(key: Keys) = state[key.ordinal]
    fun isKeyUp(key: Keys) = !isKeyDown(key)
}
