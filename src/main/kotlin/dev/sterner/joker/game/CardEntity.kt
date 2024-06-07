package dev.sterner.joker.game

import dev.sterner.joker.JokerMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class CardEntity(entityType: EntityType<*>, level: Level) : Entity(entityType, level) {

    constructor(level: Level) : this(JokerMod.CARD_ENTITY, level)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {

    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {

    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {

    }
}