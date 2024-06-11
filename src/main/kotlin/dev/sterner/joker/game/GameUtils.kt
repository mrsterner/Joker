package dev.sterner.joker.game

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.client.CardRenderer
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.core.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3i
import java.util.*

object GameUtils {

    /**
     * Returns a deck sorted deck of every rank and suit, suits * rank = 52
     */
    fun createStandardDeck(): MutableList<Card> {
        val deck = mutableListOf<Card>()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(Card(suit, rank, Special.NONE, Stamp.NONE))
            }
        }
        return deck
    }

    // Function to shuffle a deck
    fun shuffleDeck(deck: MutableList<Card>) {
        deck.shuffle(Random())
    }

    // Function to check if a hand is a flush
    fun isFlush(hand: List<Card>): Boolean {
        return hand.map { it.suit }.distinct().size == 1
    }

    // Function to check if a hand is a full house
    fun isFullHouse(hand: List<Card>): Boolean {
        val rankGroups = hand.groupBy { it.rank }.values
        return rankGroups.size == 2 && (rankGroups.any { it.size == 3 } && rankGroups.any { it.size == 2 })
    }

    // Function to check if a hand is a straight
    fun isStraight(hand: List<Card>): Boolean {
        val sortedRanks = hand.map { it.rank.value }.sorted()
        return sortedRanks.zipWithNext().all { (a, b) -> b == a + 1 }
    }

    // Function to check if a hand is a straight flush
    fun isStraightFlush(hand: List<Card>): Boolean {
        return isFlush(hand) && isStraight(hand)
    }

    // Function to check if a hand is four of a kind
    fun isFourOfAKind(hand: List<Card>): Boolean {
        return hand.groupBy { it.rank }.values.any { it.size == 4 }
    }

    // Function to check if a hand is three of a kind
    fun isThreeOfAKind(hand: List<Card>): Boolean {
        return hand.groupBy { it.rank }.values.any { it.size == 3 }
    }

    // Function to check if a hand is two pair
    fun isTwoPair(hand: List<Card>): Boolean {
        return hand.groupBy { it.rank }.values.count { it.size == 2 } == 2
    }

    // Function to check if a hand is one pair
    fun isOnePair(hand: List<Card>): Boolean {
        return hand.groupBy { it.rank }.values.any { it.size == 2 }
    }

    /**
     * Function to evaluate the best hand, return a list of every hand detected, highest first
     */
    fun evaluateHand(hand: List<Card>): List<HandRank> {
        val detectedHands = mutableListOf<HandRank>()

        if (isStraightFlush(hand)) detectedHands.add(HandRank.STRAIGHT_FLUSH)
        if (isFourOfAKind(hand)) detectedHands.add(HandRank.FOUR_OF_A_KIND)
        if (isFullHouse(hand)) detectedHands.add(HandRank.FULL_HOUSE)
        if (isFlush(hand)) detectedHands.add(HandRank.FLUSH)
        if (isStraight(hand)) detectedHands.add(HandRank.STRAIGHT)
        if (isThreeOfAKind(hand)) detectedHands.add(HandRank.THREE_OF_A_KIND)
        if (isTwoPair(hand)) detectedHands.add(HandRank.TWO_PAIR)
        if (isOnePair(hand)) detectedHands.add(HandRank.ONE_PAIR)
        if (detectedHands.isEmpty()) detectedHands.add(HandRank.HIGH_CARD)

        return detectedHands
    }

    fun writeCardToTag(card: Card): CompoundTag {
        val tag = CompoundTag()
        writeCardToTag(card, tag)
        return tag
    }

    fun writeCardToTag(card: Card, tag: CompoundTag): CompoundTag {
        tag.putString(Constants.Nbt.SUIT, card.suit.name)
        tag.putString(Constants.Nbt.RANK, card.rank.name)
        tag.putString(Constants.Nbt.SPECIAL, card.special.name)
        tag.putString(Constants.Nbt.STAMP, card.stamp.name)

        return tag
    }

    fun readCardFromTag(tag: CompoundTag): Card {
        val suit = Suit.valueOf(tag.getString(Constants.Nbt.SUIT))
        val rank = Rank.valueOf(tag.getString(Constants.Nbt.RANK))
        val special = Special.valueOf(tag.getString(Constants.Nbt.SPECIAL))
        val stamp = Stamp.valueOf(tag.getString(Constants.Nbt.STAMP))

        return Card(suit, rank, special, stamp)
    }

    fun writeDeckToTag(tag: CompoundTag, deck: MutableList<Card>): CompoundTag {
        val tagList = ListTag()

        for (card in deck) {
            val cardTag = writeCardToTag(card)
            tagList.add(cardTag)
        }

        tag.put(Constants.Nbt.DECK, tagList)
        return tag
    }

    fun readDeckFromTag(tag: CompoundTag): MutableList<Card> {
        val deck = ArrayList<Card>()

        if (tag.contains(Constants.Nbt.DECK)) {
            val listTag: ListTag = tag.getList(Constants.Nbt.DECK, 10)

            for (i in 0 until listTag.size) {
                val cardTag = listTag.getCompound(i)
                deck.add(readCardFromTag(cardTag))

            }
        }

        return deck
    }

    fun renderCard(
        guiGraphics: GuiGraphics,
        pos: Vector3i,
        scale: Float,
        pose: Quaternionf?,
        cardObject: CardObject,
        partialTick: Float
    ) {
        renderCard(
            guiGraphics,
            Vector3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()),
            scale,
            pose,
            cardObject,
            partialTick
        )
    }

    fun renderCard(
        guiGraphics: GuiGraphics,
        pos: Vector3d,
        scale: Float,
        pose: Quaternionf?,
        cardObject: CardObject,
        partialTick: Float
    ) {
        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(pos.x, pos.y, 50.0 + pos.z)
        guiGraphics.pose().scale(scale, scale, -scale)
        guiGraphics.pose().mulPose(pose)
        Lighting.setupLevel()
        val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher

        entityRenderDispatcher.setRenderShadow(false)
        RenderSystem.runAsFancy {
            CardRenderer.renderCard(
                cardObject,
                guiGraphics.pose(),
                guiGraphics.bufferSource() as MultiBufferSource,
                partialTick,
                0xF000F0
            )
        }
        guiGraphics.flush()
        entityRenderDispatcher.setRenderShadow(true)
        guiGraphics.pose().popPose()
        Lighting.setupFor3DItems()
    }

    fun writeCardObjectToTag(cardObject: CardObject): CompoundTag {
        var tag = CompoundTag()
        writeCardToTag(cardObject.card, tag)

        tag.putInt("X", cardObject.screenPos.x)
        tag.putInt("Y", cardObject.screenPos.y)
        tag.putInt("Z", cardObject.screenPos.z)

        tag.putInt("TX", cardObject.targetScreenPos.x)
        tag.putInt("TY", cardObject.targetScreenPos.y)
        tag.putInt("TZ", cardObject.targetScreenPos.z)
        return tag
    }

    fun readCardObjectFromTag(tag: CompoundTag, cardEntity: CardObject): CardObject {
        //val cardEntity = JokerMod.CARD_ENTITY.create(Minecraft.getInstance().level!!) as CardEntity

        if (tag.contains("X")) {
            val card = readCardFromTag(tag)
            cardEntity.card = card

            cardEntity.screenPos = Vector3i(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"))
            cardEntity.targetScreenPos = Vector3i(tag.getInt("TX"), tag.getInt("TY"), tag.getInt("TZ"))
        }
        return cardEntity
    }

    fun writeCardObjectsToTag(tag: CompoundTag, deck: MutableList<CardObject>): CompoundTag {
        val tagList = ListTag()

        for (card in deck) {
            val cardTag = writeCardObjectToTag(card)
            tagList.add(cardTag)
        }

        tag.put("ScreenHand", tagList)
        return tag
    }

    fun readCardObjectsFromTag(tag: CompoundTag, cardEntity: CardObject): MutableList<CardObject> {
        val deck = ArrayList<CardObject>()

        if (tag.contains("ScreenHand")) {
            val listTag: ListTag = tag.getList("ScreenHand", 10)

            for (i in 0 until listTag.size) {
                val cardTag = listTag.getCompound(i)
                deck.add(readCardObjectFromTag(cardTag, cardEntity))
            }
        }

        return deck
    }

    fun writeGameLoop(tag: CompoundTag, gameLoop: GameLoop) {
        tag.putString("GameStage", gameLoop.gameStage.name)
        tag.putInt("StageCounter", gameLoop.gameStageCounter)
        tag.putInt("SubStageCounter", gameLoop.gameSubStageCounter)
        writeCardObjectsToTag(tag, gameLoop.hand)
    }

    fun readGameLoop(playerDeckComponent: PlayerDeckComponent, tag: CompoundTag, cardEntity: CardObject): GameLoop {
        var gameLoop = GameLoop(playerDeckComponent)
        if (tag.contains("GameStage")) {
            gameLoop.gameStage = GameStage.valueOf(tag.getString("GameStage"))
            gameLoop.gameStageCounter = tag.getInt("StageCounter")
            gameLoop.gameSubStageCounter = tag.getInt("SubStageCounter")
            gameLoop.hand = readCardObjectsFromTag(tag, cardEntity)
        }

        return gameLoop
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