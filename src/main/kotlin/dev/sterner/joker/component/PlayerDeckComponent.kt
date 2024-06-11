package dev.sterner.joker.component

import dev.sterner.joker.core.Card
import dev.sterner.joker.game.CardObject
import dev.sterner.joker.game.GameLoop
import dev.sterner.joker.game.GameUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent

class PlayerDeckComponent(val player: Player) : AutoSyncedComponent, ClientTickingComponent {

    //Stored player deck
    var deck: MutableList<Card> = GameUtils.createStandardDeck()

    //Deck loaded for game
    var gameDeck: MutableList<Card> = deck

    var hand: MutableList<Card> = ArrayList()
    var totalHandSize: Int = 9

    var gameOn: Boolean = false
    var gameLoop: GameLoop = GameLoop(this)

    var maxHands: Int = 4
    var hands: Int = maxHands
    var maxDiscards: Int = 0
    var discards: Int = maxDiscards
    var money: Int = 0

    override fun clientTick() {
        if (gameOn) {
            gameLoop.tick()
        } else {
            gameLoop.reset()
        }
    }

    fun pickRandomCardAndRemove(gameDeck: MutableList<Card>): Card {
        gameDeck.shuffle()
        JokerComponents.DECK.sync(player)
        val card = gameDeck.removeFirst()
        return card
    }

    @Deprecated("")
    fun addCard(card: Card) {
        deck.add(card)
        JokerComponents.DECK.sync(player)
    }

    @Deprecated("")
    fun removeCard(card: Card) {
        deck.remove(card)
        JokerComponents.DECK.sync(player)
    }

    override fun readFromNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        deck = GameUtils.readDeckFromTag(tag)
        if (Minecraft.getInstance().level != null) {
            gameLoop = GameUtils.readGameLoop(this, tag, CardObject())
        }

        gameOn = tag.getBoolean("GameOn")
        hands = tag.getInt("Hands")
        discards = tag.getInt("Discards")
        money = tag.getInt("Money")
    }

    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        GameUtils.writeDeckToTag(tag, deck)
        GameUtils.writeGameLoop(tag, gameLoop)
        tag.putBoolean("GameOn", gameOn)
        tag.putInt("Hands", hands)
        tag.putInt("Discards", discards)
        tag.putInt("Money", money)
    }
}