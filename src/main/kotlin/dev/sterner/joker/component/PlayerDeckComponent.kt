package dev.sterner.joker.component

import dev.sterner.joker.JokerMod
import dev.sterner.joker.core.Card
import dev.sterner.joker.core.GameUtils
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent

class PlayerDeckComponent(val player: Player) : AutoSyncedComponent {

    //Stored player deck
    var deck: MutableList<Card> = GameUtils.createStandardDeck()

    //Deck loaded for game
    var gameDeck: MutableList<Card> = ArrayList()
    var discardPile: MutableList<Card> = ArrayList()

    var hand: MutableList<Card> = ArrayList()
    var handSize: Int = 0
    var totalHandSize: Int = 9

    fun drawHand(deck: MutableList<Card>) {
        for (i in 0 until totalHandSize - handSize) {
            val card = deck.random()
            deck.remove(card)
            hand.add(card)
        }
    }

    fun addCard(card: Card) {
        deck.add(card)
        JokerMod.DECK.sync(player)
    }

    fun removeCard(card: Card) {
        deck.remove(card)
        JokerMod.DECK.sync(player)
    }

    override fun readFromNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        deck = GameUtils.readDeckFromTag(tag)
    }

    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        GameUtils.writeDeckToTag(tag, deck)
    }
}