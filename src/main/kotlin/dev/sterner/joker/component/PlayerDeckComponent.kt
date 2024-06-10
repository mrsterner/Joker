package dev.sterner.joker.component

import dev.sterner.joker.JokerMod
import dev.sterner.joker.core.Card
import dev.sterner.joker.core.GameUtils
import dev.sterner.joker.game.CardObject
import dev.sterner.joker.game.GameLoop
import net.minecraft.client.Minecraft
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent

class PlayerDeckComponent(val player: Player) : AutoSyncedComponent, CommonTickingComponent  {

    //Stored player deck
    var deck: MutableList<Card> = GameUtils.createStandardDeck()

    //Deck loaded for game
    var gameDeck: MutableList<Card> = deck

    var hand: MutableList<Card> = ArrayList()
    var totalHandSize: Int = 9

    var gameOn: Boolean = false
    var gameLoop: GameLoop = GameLoop(this)

    override fun tick() {
        if (gameOn) {
            gameLoop.tick()
        } else {
            gameLoop.reset()
        }
    }

    fun pickRandomCardAndRemove(gameDeck: MutableList<Card>): Card {
        gameDeck.shuffle()
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
    }

    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        GameUtils.writeDeckToTag(tag, deck)
        GameUtils.writeGameLoop(tag, gameLoop)
        tag.putBoolean("GameOn", gameOn)
    }




}