package dev.sterner.joker.game

import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.core.Card
import net.minecraft.client.Minecraft
import org.joml.Vector3i
import kotlin.math.abs

class GameLoop(val component: PlayerDeckComponent) {

    var gameStage: GameStage = GameStage.SETUP
    var gameStageCounter: Int = 0
    var gameSubStageCounter: Int = 0

    var screenHeight = Minecraft.getInstance().window.guiScaledHeight
    var screenWidth = Minecraft.getInstance().window.guiScaledWidth

    var hand: MutableList<CardObject> = ArrayList()
    var playedHand: MutableList<CardObject> = ArrayList()

    var handLevelY = 40
    var handLevelX = 128 + 32

    var playedHandLevelY = 96 + 32
    var playedHandLevelX = 128 + 72

    var handSize = 0

    var isDiscarding = false
    var isPlaying = false
    var discardAnimationTick = 0
    var playAnimationTick = 0

    var orderByRank = true

    /**
     * Game tick, this ticks cards and theirs position, rotation
     * This also updates game stages
     */
    fun tick() {
        updateCards(hand)
        updateCards(playedHand)
        updateGameStage()
    }

    /**
     * CardObjects have internal tick functions, they are hooked to the game tick here
     */
    private fun updateCards(cards: List<CardObject>) {
        for (card in cards) {
            if (!card.isHolding) {
                card.tick(Minecraft.getInstance().timer.gameTimeDeltaTicks)
            }
        }
    }

    /**
     * Ticks the current GameStage
     */
    private fun updateGameStage() {
        when (gameStage) {
            GameStage.SETUP -> handleSetupStage()
            GameStage.CHOICE_PHASE -> handleChoicePhase()
            GameStage.RESTOCK_PHASE -> handleRestockPhase()
            GameStage.PLAY_PHASE -> handlePlayPhase()
            else -> {}
        }
    }

    /**
     * The initial stage of the game, will draw a hand in tickOnSetup
     */
    private fun handleSetupStage() {
        gameStageCounter++
        if (tickOnSetup(component.gameDeck, component.totalHandSize) && gameStageCounter >= gameStage.time) {
            gameStageCounter = 0
            endTickOn(gameStage)
            gameStage = GameStage.CHOICE_PHASE
            startTickOn(gameStage)
        }
    }

    /**
     * isDiscard and isPlaying is handled with respective widget and will trigger the choice phase's ticker
     */
    private fun handleChoicePhase() {
        if (isDiscarding || isPlaying) {
            gameStageCounter++
            if (tickOnChoice() && gameStageCounter >= gameStage.time) {
                gameStageCounter = 0
                endTickOn(gameStage)
                gameStage = GameStage.RESTOCK_PHASE
                startTickOn(gameStage)
            }
        }
    }

    /**
     * Selected at the end of a Choice phase to refill the hand with its capacity of cards
     */
    private fun handleRestockPhase() {
        gameStageCounter++
        if (tickOnSetup(component.gameDeck, component.totalHandSize) && gameStageCounter >= gameStage.time) {
            gameStageCounter = 0
            endTickOn(gameStage)
            if (isPlaying) {
                isPlaying = false
                gameStage = GameStage.PLAY_PHASE
            } else {
                gameStage = GameStage.CHOICE_PHASE
            }
            startTickOn(gameStage)
        }
    }

    /**
     * Cards in the playedHand runs their animation and activation here
     */
    private fun handlePlayPhase() {
        gameStageCounter++
        if (tickOnPlay() && gameStageCounter >= gameStage.time) {
            gameStageCounter = 0
            endTickOn(gameStage)
            gameStage = GameStage.EVAL_PHASE
            startTickOn(gameStage)
        }
    }

    /**
     * Depending on discarding or playing a set of selected cards, this will either play a discard animation and
     * call for discard, or, ...
     */
    private fun tickOnChoice(): Boolean {
        if (isDiscarding) {
            discardAnimationTick++
            if (discardAnimationTick == 1) {
                raiseSelectedCards()
            }
            if (discardAnimationTick == 10) {
                discardSelectedCards()
            }
            return true
        }

        if (isPlaying) {
            playAnimationTick++
            if (playAnimationTick == 1) {
                moveSelectedCardsToPlayedHand()
                reorderHandByPos(playedHand, playedHandLevelY, false, screenWidth / 6, playedHandLevelX)
                reorderHandByPos()
            }
            return true
        }

        return false
    }

    private fun tickOnPlay(): Boolean {
        return false
    }

    private fun endTickOn(gameStage: GameStage) {
        if (gameStage == GameStage.CHOICE_PHASE) {
            if (isDiscarding) {
                hand.removeAll { it.isSelected }
                handSize = hand.size
                isDiscarding = false
                discardAnimationTick = 0
            }
        }
    }

    private fun startTickOn(gameStage: GameStage) {
        if (gameStage == GameStage.PLAY_PHASE) {
            reorderHandByRankOrSuit()
        }

        if (gameStage == GameStage.CHOICE_PHASE) {
            reorderHandByRankOrSuit()
        }
    }

    fun reorderHandByRankOrSuit() {
        if (orderByRank) {
            hand.sortByDescending { it.card.rank}
        } else {
            hand.sortByDescending { it.card.suit }
        }

        JokerComponents.DECK.sync(component.player)
        positionHandCards()
    }

    private fun positionHandCards() {
        val points = calculateEquallySpacedPoints(handSize)
        val arcHeight = 6 // Maximum height adjustment for the arc
        val centerIndex = (hand.size - 1) / 2.0 // Center index for the arc

        for ((counter, space) in points.withIndex()) {
            val yOffset = ((centerIndex - abs(counter - centerIndex)) / centerIndex) * arcHeight
            val isSelected = hand[counter].isSelected
            val pos = Vector3i(
                handLevelX + space,
                screenHeight - handLevelY - yOffset.toInt() - if (isSelected) 8 else 0,
                counter * 10
            )
            hand[counter].targetScreenPos = pos
        }
    }

    private fun tickOnSetup(gameDeck: MutableList<Card>, totalHandSize: Int): Boolean {
        fillHand(gameDeck, totalHandSize)
        return handSize == totalHandSize
    }

    private fun fillHand(gameDeck: MutableList<Card>, totalHandSize: Int) {
        if (handSize < totalHandSize) {
            gameSubStageCounter++
            if (gameSubStageCounter >= 20 * 0.2) {
                gameSubStageCounter = 0
                val cardEntity = CardObject()
                cardEntity.card = component.pickRandomCardAndRemove(gameDeck)
                cardEntity.screenPos = Vector3i(screenWidth - 50, screenHeight - 40, 20)
                cardEntity.targetScreenPos = calculateTargetPosition(hand.size, totalHandSize)
                hand.add(cardEntity)
                handSize++
                JokerComponents.DECK.sync(component.player)
            }
        }
    }

    private fun calculateTargetPosition(index: Int, totalHandSize: Int): Vector3i {
        val points = calculateEquallySpacedPoints(totalHandSize)
        val arcHeight = 6
        val centerIndex = (totalHandSize - 1) / 2.0
        val yOffset = ((centerIndex - abs(index - centerIndex)) / centerIndex) * arcHeight

        return Vector3i(handLevelX + points[index], screenHeight - handLevelY - yOffset.toInt(), index * 4)
    }

    private fun moveSelectedCardsToPlayedHand() {
        hand.removeAll { it.isSelected.also { isSelected -> if (isSelected) playedHand.add(it) } }
        playedHand.forEach {
            handSize--
            it.isSelected = false
            it.rotationZ = 0f
            it.targetRotationZ = 0f
        }
    }

    private fun discardSelectedCards() {
        hand.filter { it.isSelected }.forEach {
            it.isDiscarded = true
            it.targetScreenPos = Vector3i(screenWidth + 260, it.screenPos.y - 30, it.screenPos.z)
            it.targetRotationZ = it.rotationZ + 20
        }
    }

    private fun raiseSelectedCards() {
        hand.filter { it.isSelected }.forEach {
            it.targetScreenPos = Vector3i(it.screenPos.x, it.screenPos.y - 20, it.screenPos.z)
        }
    }

    fun reorderHandByPos() {
        reorderHandByPos(hand, handLevelY, true, (screenWidth / 2.5).toInt(), handLevelX)
    }

    private fun reorderHandByPos(hand: MutableList<CardObject>, handYLevel: Int, arch: Boolean, width: Int, handLevelX: Int) {
        hand.sortBy { it.screenPos.x }
        JokerComponents.DECK.sync(component.player)
        val points = calculateEquallySpacedPoints(hand.size, width)
        val arcHeight = 6
        val centerIndex = (hand.size - 1) / 2.0

        hand.forEachIndexed { index, card ->
            val yOffset = if (arch) ((centerIndex - abs(index - centerIndex)) / centerIndex) * arcHeight else 0
            val pos = Vector3i(
                handLevelX + points[index],
                screenHeight - handYLevel - yOffset.toInt() - if (card.isSelected) 12 else 0,
                index * 10
            )
            card.targetScreenPos = pos
        }
    }

    private fun calculateEquallySpacedPoints(handSize: Int): List<Int> {
        return calculateEquallySpacedPoints(handSize, (screenWidth / 2.5).toInt())
    }

    private fun calculateEquallySpacedPoints(handSize: Int, width: Int): List<Int> {
        val spacing = width / (handSize - 1)
        return List(handSize) { it * spacing }
    }

    fun reset() {
        gameStage = GameStage.SETUP
        gameStageCounter = 0
        gameSubStageCounter = 0
        discardAnimationTick = 0
        playAnimationTick = 0
        handSize = 0
        hand.clear()
        playedHand.clear()
        isDiscarding = false
        isPlaying = false
        orderByRank = true
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