package dev.sterner.joker.game

import dev.sterner.joker.JokerMod
import dev.sterner.joker.core.Card
import dev.sterner.joker.core.GameUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class CardEntity(entityType: EntityType<*>, level: Level) : Entity(entityType, level) {

    var card: Card? = null

    constructor(level: Level) : this(JokerMod.CARD_ENTITY, level)

    fun getCardName(card: Card): String {
        return card.rank.name.lowercase() + "_of_" + card.suit.name.lowercase()
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {

    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        card = GameUtils.readCardFromTag(compoundTag)
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        if (card != null) {
            GameUtils.writeCardToTag(card!!, compoundTag)
        }
    }
}