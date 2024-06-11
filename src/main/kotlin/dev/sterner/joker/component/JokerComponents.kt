package dev.sterner.joker.component

import dev.sterner.joker.JokerMod.id
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy

class JokerComponents : EntityComponentInitializer {


    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(DECK, { player -> PlayerDeckComponent(player) }, RespawnCopyStrategy.INVENTORY)
    }

    companion object {
        val DECK: ComponentKey<PlayerDeckComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(
            id("deck"),
            PlayerDeckComponent::class.java
        )
    }
}