package dev.sterner.joker.game

import dev.sterner.joker.core.*

class GameLoop {

    private val game: GameStage = GameStage.CHOICE_PHASE
    private val gameStageCounter: Int = 0

    fun tick() {

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