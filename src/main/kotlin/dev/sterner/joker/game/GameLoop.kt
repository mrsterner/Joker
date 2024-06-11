package dev.sterner.joker.game

import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.core.Card
import net.minecraft.client.Minecraft
import org.joml.Vector3i
import kotlin.math.abs

class GameLoop(val component: PlayerDeckComponent) {

    var gameStage: GameStage = GameStage.NONE
    var gameStageCounter: Int = 0
    var gameSubStageCounter: Int = 0

    var screenHeight = Minecraft.getInstance().window.guiScaledHeight
    var screenWidth = Minecraft.getInstance().window.guiScaledWidth

    var hand: MutableList<CardObject> = ArrayList()
    var handLevelY = 40
    var handLevelX = 128 + 32

    var handSize = 0

    var isDiscarding = false
    var isPlaying = false
    var discardAnimationTick = 0

    var orderByRank = true

    fun tick() {
        for (cardObject in hand) {
            if (!cardObject.isHolding) {
                cardObject.tick(Minecraft.getInstance().timer.gameTimeDeltaTicks)
            }
        }

        if (gameStage == GameStage.NONE) {
            gameStageCounter++
            val bl: Boolean = tickOnNone(component.gameDeck, component.totalHandSize)
            if (bl && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.CHOICE_PHASE
            }
        }

        if (gameStage == GameStage.CHOICE_PHASE && (isDiscarding || isPlaying)) {
            gameStageCounter++
            val bl: Boolean = tickOnChoice(component.gameDeck, component.totalHandSize)
            if (bl && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                endTickOnChoice(component.gameDeck, component.totalHandSize)
                gameStage = GameStage.RESTOCK_PHASE
                isDiscarding = false
                isPlaying = false
            }
        }

        if (gameStage == GameStage.RESTOCK_PHASE) {
            gameStageCounter++
            val bl: Boolean = tickOnNone(component.gameDeck, component.totalHandSize)
            if (bl && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.CHOICE_PHASE
                reorderHandByRankOrSuit()
            }
        }
    }

    fun reorderHandByRankOrSuit() {
        if (orderByRank) {
            hand.sortBy { it.card.rank }
        } else {
            hand.sortBy { it.card.suit }
        }

        JokerComponents.DECK.sync(component.player)
        val point: List<Int> = calculateEquallySpacedPoints(handSize)
        val arcHeight = 6 // Maximum height adjustment for the arc
        val centerIndex = (hand.size - 1) / 2.0 // Center index for the arc

        for ((counter, space) in point.withIndex()) {
            val bl: Boolean = hand[counter].isSelected
            val yOffset = ((centerIndex - Math.abs(counter - centerIndex)) / centerIndex) * arcHeight

            val pos = Vector3i(
                handLevelX + space,
                this.screenHeight - handLevelY - yOffset.toInt() - if (bl) 8 else 0,
                counter * 10
            )
            hand[counter].targetScreenPos = pos
        }
    }


    fun tickOnNone(gameDeck: MutableList<Card>, totalHandSize: Int): Boolean {

        fillHand(gameDeck, totalHandSize)

        return handSize == totalHandSize
    }

    private fun fillHand(gameDeck: MutableList<Card>, totalHandSize: Int) {
        if (handSize < totalHandSize) {
            val point = calculateEquallySpacedPoints(totalHandSize)
            val handDSize = hand.size
            val arcHeight = 6 // Maximum height adjustment for the arc
            val centerIndex = (totalHandSize - 1) / 2.0 // Center index for the arc

            val index = handDSize
            val yOffset = ((centerIndex - abs(index - centerIndex)) / centerIndex) * arcHeight

            val pos = Vector3i(handLevelX + point[handDSize], this.screenHeight - handLevelY - yOffset.toInt(), handDSize * 4)

            gameSubStageCounter++
            if (gameSubStageCounter >= 20 * 0.2) {
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
    }

    fun tickOnChoice(gameDeck: MutableList<Card>, totalHandSize: Int): Boolean {
        if (isDiscarding) {
            discardAnimationTick++
            if (discardAnimationTick == 1) {
                raiseSelectedCards()
            }

            if (discardAnimationTick > 10) {
                discardSelectedCards()
                return true
            }
        }

        return false
    }

    private fun endTickOnChoice(gameDeck: MutableList<Card>, totalHandSize: Int) {
        if (isDiscarding) {
            hand.removeAll { it.isSelected }
            handSize = hand.size
            isDiscarding = false
            discardAnimationTick = 0
        }

        if (isPlaying) {

        }


    }

    fun discardSelectedCards() {
        for (cardObject in hand) {
            if (cardObject.isSelected) {
                cardObject.isDiscarded = true
                cardObject.targetScreenPos = Vector3i(this.screenWidth + 260, cardObject.screenPos.y - 30, cardObject.screenPos.z)
                cardObject.targetRotationZ = cardObject.rotationZ + 20
            }
        }
    }

    fun raiseSelectedCards() {
        for (cardObject in hand) {
            if (cardObject.isSelected) {
                cardObject.targetScreenPos = Vector3i(cardObject.screenPos.x, cardObject.screenPos.y - 20, cardObject.screenPos.z)
            }
        }
    }

    fun reorderHandByPos() {
        hand.sortBy { it.screenPos.x }
        JokerComponents.DECK.sync(component.player)
        val point: List<Int> = calculateEquallySpacedPoints(handSize)
        val arcHeight = 6 // Maximum height adjustment for the arc
        val centerIndex = (hand.size - 1) / 2.0 // Center index for the arc

        for ((counter, space) in point.withIndex()) {
            val bl: Boolean = hand[counter].isSelected
            val yOffset = ((centerIndex - Math.abs(counter - centerIndex)) / centerIndex) * arcHeight

            val pos = Vector3i(
                handLevelX + space,
                this.screenHeight - handLevelY - yOffset.toInt() - if (bl) 8 else 0,
                counter * 10
            )
            hand[counter].targetScreenPos = pos
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
}