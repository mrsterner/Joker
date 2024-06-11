package dev.sterner.joker.game

import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.core.*
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft
import org.joml.Vector3i

class GameLoop(val component: PlayerDeckComponent) {

    var gameStage: GameStage = GameStage.NONE
    var gameStageCounter: Int = 0
    var gameSubStageCounter: Int = 0

    var screenHeight = Minecraft.getInstance().window.guiScaledHeight
    var screenWidth = Minecraft.getInstance().window.guiScaledWidth

    var hand: MutableList<CardObject> = ArrayList()
    var handLevelY = 30
    var handLevelX = 128 + 32

    var handSize = 0

    fun tick() {
        for (cardObject in hand) {
            if (!cardObject.isHolding) {
                cardObject.tick(Minecraft.getInstance().timer.gameTimeDeltaTicks)
                JokerComponents.DECK.sync(component.player)
            }
        }

        if (gameStage == GameStage.NONE) {
            gameStageCounter++
            val bl: Boolean = tickOnNone(component.gameDeck, component.totalHandSize, gameStageCounter)
            if (bl && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.CHOICE_PHASE
            }
        }

        if (gameStage == GameStage.CHOICE_PHASE) {
            gameStageCounter++
            tickOnChoice(component.gameDeck, component.totalHandSize, gameStageCounter)
            if (gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.PLAY_PHASE
            }
        }
    }

    fun tickOnNone(gameDeck: MutableList<Card>, totalHandSize: Int, gameStageCounter: Int): Boolean {
        if (handSize < totalHandSize) {
            val point = calculateEquallySpacedPoints(totalHandSize)
            val handDSize = hand.size
            val arcHeight = 10 // Maximum height adjustment for the arc
            val centerIndex = (totalHandSize - 1) / 2.0 // Center index for the arc

            val index = handDSize
            val yOffset = ((centerIndex - Math.abs(index - centerIndex)) / centerIndex) * arcHeight

            val pos = Vector3i(handLevelX + point[handDSize], this.screenHeight - handLevelY - yOffset.toInt(), handDSize * 4)

            gameSubStageCounter++
            if (gameSubStageCounter >= 20 * 0.25) {
                gameSubStageCounter = 0
                val card = component.pickRandomCardAndRemove(gameDeck)
                val cardEntity = CardObject()
                cardEntity.card = card
                cardEntity.screenPos = Vector3i(this.screenWidth - 50, this.screenHeight - 40, 20)
                cardEntity.targetScreenPos = pos

                hand.add(cardEntity)
                handSize++

                JokerComponents.DECK.sync(component.player)
            }
        }

        if (handSize == totalHandSize) {
            return true
        }

        return false
    }

    fun tickOnChoice(gameDeck: MutableList<Card>, totalHandSize: Int, gameStageCounter: Int): Boolean {
        return true
    }

    fun reorderHand() {
        hand.sortBy { it.screenPos.x }
        JokerComponents.DECK.sync(component.player)
        val point: List<Int> = calculateEquallySpacedPoints(component.totalHandSize)
        val arcHeight = 10 // Maximum height adjustment for the arc
        val centerIndex = (hand.size - 1) / 2.0 // Center index for the arc

        var counter = 0
        for (space in point) {
            val bl: Boolean = hand[counter].isSelected
            val yOffset = ((centerIndex - Math.abs(counter - centerIndex)) / centerIndex) * arcHeight

            var pos = Vector3i(handLevelX + space, this.screenHeight - handLevelY - yOffset.toInt(), counter * 10)
            if (bl) {
                pos = Vector3i(pos.x, pos.y - 8, pos.z)
            }
            hand[counter].targetScreenPos = pos
            counter++
        }
    }

    fun calculateEquallySpacedPoints(handSize: Int): List<Int> {
        val width = (this.screenWidth / 2.5).toInt()
        val spacing = width / (handSize - 1)

        val points = mutableListOf<Int>()
        for (i in 0 until handSize) {
            points.add(i * spacing)
        }

        return points
    }

    fun sellCard(card: Card): JokerContext {
        return JokerContext()
    }

    fun useTarot(tarot: Tarot): JokerContext {
        return JokerContext()
    }

    fun usePlanet(planet: Planet): JokerContext {
        return JokerContext()
    }

    fun sortByRank(hand: List<Card>): List<Card> {
        return hand.sortedWith(compareByDescending<Card> { it.rank })
    }

    fun sortBySuit(hand: List<Card>): List<Card> {
        return hand.sortedWith(compareByDescending<Card> { it.suit })
    }

    fun evaluateCard(card: Card): JokerContext {
        return JokerContext()
    }

    fun checkJoker(card: Card, joker: Joker): JokerContext {
        return JokerContext()
    }

    fun checkJoker(playedHand: List<Card>, joker: Joker): JokerContext {
        return JokerContext()
    }

    fun checkHand(hand: List<Card>, joker: Joker): JokerContext {
        return JokerContext()
    }

    fun triggerBlindAbility(blind: Blind): JokerContext {
        return JokerContext()
    }

    fun checkStamp(card: Card): JokerContext {
        return JokerContext()
    }

    fun checkSpecial(card: Card): JokerContext {
        return JokerContext()
    }



    fun reset() {
        gameStage = GameStage.NONE
        gameStageCounter = 0
        gameSubStageCounter = 0
        handSize = 0
        hand.clear()
    }

    /** Game loop
     *
    1. Select hand of 1 - 5 cards -> 2.
    1.1 use a Tarot
    1.2 use a Planet
    1.3 sell a Joker, Tarot or Planet
    1.4 Rearrange Jokers
    1.5 Rearrange Cards'
    1.6 Sort cards by suit or rank

    2. Play hand -> 4.
    - Trigger Blind ability
    2.1 Discard hand -> 3.
    - Trigger Blind ability

    3. Draw -> 1.
    - Trigger Blind ability

    4. Start Evaluating
    - Evaluate 1st card -> 5.
    - Evaluate 2nd card -> 5.
    - Evaluate 3rd card -> 5
    - Evaluate 4th card -> 5
    - Evaluate last card -> 5.1

    5. Check value of card, and multiply with hand rank
    - Trigger Blind ability
    - Check card special ability -> 7.
    - Check stamp -> 8.
    - Check Joker for ability and apply -> 9.
    - Resolve points -> 10.

    6. Add hand ability -> 5.

    7. ???

    8. ???

    9. ???

    10. Check jokers for resolve -> 11.

    11. Check win condition

     */

    fun formatVector3i(vector: Vector3i): String {
        return String.format("( %.1f %.1f %.1f )", vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
    }

}