package dev.sterner.joker.game

import dev.sterner.joker.client.CardScreenObject
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.core.*
import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import org.joml.Vector3i

class GameLoop(val component: PlayerDeckComponent) {

    var gameStage: GameStage = GameStage.NONE
    var gameStageCounter: Int = 0
    var gameSubStageCounter: Int = 0

    var screenHeight = Minecraft.getInstance().window.guiScaledHeight
    var screenWidth = Minecraft.getInstance().window.guiScaledWidth

    var hand: MutableList<CardScreenObject> = ArrayList()
    var handLevelY = 30
    var handLevelX = 128 + 32

    fun tick() {
        if (gameStage == GameStage.NONE) {
            println("None")
            gameStageCounter++
            val bl: Boolean = tickOnNone(component.gameDeck, component.handSize, component.totalHandSize, gameStageCounter)
            if (bl && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.CHOICE_PHASE
            }
        }

        if (gameStage == GameStage.CHOICE_PHASE) {
            println("Choice")
            gameStageCounter++
            tickOnChoice(component.gameDeck, component.handSize, component.totalHandSize, gameStageCounter)
            if (gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                gameStage = GameStage.PLAY_PHASE
            }
        }
    }

    fun tickOnNone(gameDeck: MutableList<Card>, handSize: Int, totalHandSize: Int, gameStageCounter: Int): Boolean {

        for (cardObject in hand) {
            cardObject.tick(0.5f)
        }

        if (handSize < totalHandSize) {

            val point = calculateEquallySpacedPoints(handSize)

            val pos = Vector3i(handLevelX + point[handSize], this.screenHeight - handLevelY, handSize)

            gameSubStageCounter++
            if (gameSubStageCounter >= 20 * 2) {
                gameSubStageCounter = 0
                val card = component.pickRandomCardAndRemove(gameDeck)
                val cardEntity = CardEntity(Minecraft.getInstance().level!!)
                cardEntity.card = card
                val cardScreenObject = CardScreenObject()
                cardScreenObject.centerPos = Vector3i(30,30,0)
                cardScreenObject.targetPos = pos
                println("TargetPos: " + cardScreenObject.targetPos + " : " + handSize)
                hand.add(cardScreenObject)
            }
        }

        if (handSize == totalHandSize) {
            return true
        }


        return false
    }

    fun tickOnChoice(gameDeck: MutableList<Card>, handSize: Int, totalHandSize: Int, gameStageCounter: Int): Boolean {


        return true
    }

    fun reorderHand() {
        hand?.sortBy { it.centerPos.x }
        val point: List<Int> = calculateEquallySpacedPoints(component.handSize)
        for ((j, i) in point.indices.withIndex()) {
            val pos = Vector3i(handLevelX + point[i], this.screenHeight - handLevelY, j)
            hand!![i].centerPos = pos
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