package dev.sterner.joker.core

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
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

    fun writeCardToTag(card: Card) : CompoundTag {
        val tag = CompoundTag()
        writeCardToTag(card, tag)
        return tag
    }

    fun writeCardToTag(card: Card, tag: CompoundTag) : CompoundTag {
        tag.putString(Constants.Nbt.SUIT, card.suit.name)
        tag.putString(Constants.Nbt.RANK, card.rank.name)
        tag.putString(Constants.Nbt.SPECIAL, card.special.name)
        tag.putString(Constants.Nbt.STAMP, card.stamp.name)

        return tag
    }

    fun readCardFromTag(tag: CompoundTag) : Card {
        val suit = Suit.valueOf(tag.getString(Constants.Nbt.SUIT))
        val rank = Rank.valueOf(tag.getString(Constants.Nbt.RANK))
        val special = Special.valueOf(tag.getString(Constants.Nbt.SPECIAL))
        val stamp = Stamp.valueOf(tag.getString(Constants.Nbt.STAMP))

        return Card(suit, rank, special, stamp)
    }

    fun writeDeckToTag(tag: CompoundTag, deck: MutableList<Card>) : CompoundTag {
        val tagList = ListTag()

        for (card in deck) {
            val cardTag = writeCardToTag(card)
            tagList.add(cardTag)
        }

        tag.put(Constants.Nbt.DECK, tagList)
        return tag
    }

    fun readDeckFromTag(tag: CompoundTag) : MutableList<Card> {
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