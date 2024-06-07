package dev.sterner.joker.component

import dev.sterner.joker.core.Card
import dev.sterner.joker.core.GameUtils
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent

class PlayerDeckComponent(final val player: Player) : AutoSyncedComponent {

    var deck: MutableList<Card> = GameUtils.createStandardDeck()

    override fun readFromNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        deck = GameUtils.readDeckFromTag(tag)
    }

    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        GameUtils.writeDeckToTag(tag, deck)
    }
}