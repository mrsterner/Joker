package dev.sterner.joker.game

import dev.sterner.joker.core.Card
import dev.sterner.joker.core.Special
import org.joml.Vector3i

class CardObject {
    var isHolding: Boolean = false
    val width: Int = 31
    val height: Int = 45
    var card: Card? = null
    var screenPos = Vector3i(0,0,0)
    var targetScreenPos = Vector3i(0,0,0)

    fun getCardName(card: Card): String {
        return card.rank.name.lowercase() + "_of_" + card.suit.name.lowercase()
    }

    fun getBaseName(card: Card): String {
        if (card.special == Special.NONE || card.special == Special.WILD) {
            return "base"
        }
        return card.special.name.lowercase() + "_base"
    }

    fun tick(fl: Float) {
        if (screenPos != targetScreenPos) {
            screenPos = lerp(screenPos, targetScreenPos, fl)
        }
    }

    fun easeOut(t: Float): Float {
        return t * (2 - t)
    }

    fun lerp(a: Vector3i, b: Vector3i, t: Float): Vector3i {
        val easedT = easeOut(t)
        val x = a.x + (easedT * (b.x - a.x)).toInt()
        val y = a.y + (easedT * (b.y - a.y)).toInt()
        val z = a.z + (easedT * (b.z - a.z)).toInt()
        return Vector3i(x, y, z)
    }
}