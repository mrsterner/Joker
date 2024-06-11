package dev.sterner.joker.game

import dev.sterner.joker.core.*
import net.minecraft.util.Mth
import org.joml.Vector3i
import kotlin.math.roundToInt

class CardObject {

    var card: Card = Card(Suit.SPADES, Rank.ACE, Special.NONE, Stamp.NONE)
    val width: Int = 31
    val height: Int = 45

    var isHolding: Boolean = false
    var isSelected: Boolean = false

    var screenPos = Vector3i(0, 0, 0)
    var targetScreenPos = Vector3i(0, 0, 0)

    var rotationY: Float = 180f // Initial rotation is 0 degrees (face down)
    var targetRotationY: Float = 0f // Target rotation is 180 degrees (face up)


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
            //println("y: " + screenPos.y + ", z: " + screenPos.z)
            screenPos = lerp(screenPos, targetScreenPos, fl)
            rotationY = Mth.lerp(fl, rotationY, targetRotationY)
        }
    }

    fun easeOut(t: Float): Float {
        return t * (2 - t)
    }

    fun lerp(a: Vector3i, b: Vector3i, t: Float): Vector3i {
        val easedT = easeOut(t)
        val x = a.x + (easedT * (b.x - a.x)).roundToInt()
        val y = a.y + (easedT * (b.y - a.y)).roundToInt()
        val z = a.z + (easedT * (b.z - a.z)).roundToInt()
        return Vector3i(x, y, z)
    }
}