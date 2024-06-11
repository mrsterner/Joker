package dev.sterner.joker.game

import dev.sterner.joker.core.*
import net.minecraft.util.Mth
import org.joml.Vector3i
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CardObject {

    var card: Card = Card(Suit.SPADES, Rank.ACE, Special.NONE, Stamp.NONE)
    val width: Int = 31
    val height: Int = 45

    var isHolding: Boolean = false
    var isSelected: Boolean = false
    var isDiscarded: Boolean = false

    var screenPos = Vector3i(0, 0, 0)
    var targetScreenPos = Vector3i(0, 0, 0)

    var rotationY: Float = 180f // Initial rotation is 0 degrees (face down)
    var targetRotationY: Float = 0f // Target rotation is 180 degrees (face up)

    var rotationZ: Float = 0f
    var targetRotationZ: Float = 0f


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
            if (!isDiscarded) {
                screenPos = lerp(screenPos, targetScreenPos, fl)
                rotationY = Mth.lerp(fl, rotationY, targetRotationY)
            } else {
                screenPos = lerpArc(screenPos, targetScreenPos, fl)
                rotationZ = Mth.lerp(fl, rotationZ, targetRotationZ)
            }
            
        }
    }


    private fun lerpArc(screenPos: Vector3i, targetScreenPos: Vector3i, fl: Float): Vector3i {
        val controlPoint = Vector3i(
            (screenPos.x + targetScreenPos.x) / 2,
            max(screenPos.y, targetScreenPos.y) + 30, // Ensure the control point is above
            (screenPos.z + targetScreenPos.z) / 2
        )

        // Adjust the speed factor to make the lerp faster
        val speedFactor = 0.5f
        val t = easeOut(fl * speedFactor)

        val x = (1 - t) * (1 - t) * screenPos.x + 2 * (1 - t) * t * controlPoint.x + t * t * targetScreenPos.x
        val y = (1 - t) * (1 - t) * screenPos.y + 2 * (1 - t) * t * controlPoint.y + t * t * targetScreenPos.y
        val z = (1 - t) * (1 - t) * screenPos.z + 2 * (1 - t) * t * controlPoint.z + t * t * targetScreenPos.z

        return Vector3i(x.toInt(), y.toInt(), z.toInt())
    }

    private fun easeIn(t: Float): Float {
        // Cubic ease-in function for faster transition
        return t * t * t
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