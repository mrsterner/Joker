package dev.sterner.joker.client

import dev.sterner.joker.core.Card
import dev.sterner.joker.game.CardEntity
import org.joml.Vector3i
import java.awt.Point

class CardScreenObject {
    var centerPoint: Vector3i = Vector3i(0,0,0)
    var card: CardEntity? = null
    var width: Int? = 31
    var height = 45
}