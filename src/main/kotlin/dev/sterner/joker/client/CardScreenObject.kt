package dev.sterner.joker.client

import dev.sterner.joker.core.Card
import dev.sterner.joker.game.CardEntity
import net.minecraft.util.Mth
import org.joml.Vector3i
import java.awt.Point

class CardScreenObject {
    var centerPos: Vector3i = Vector3i(0,0,0)
    var targetPos: Vector3i = Vector3i(0,0,0)
    var cardEntity: CardEntity? = null
    var width: Int? = 31
    var height = 45

    fun tick(partialTick: Float){
        if (centerPos != targetPos) {
            centerPos = lerp(centerPos, targetPos, partialTick)
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