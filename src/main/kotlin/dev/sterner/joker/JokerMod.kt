package dev.sterner.joker

import dev.sterner.joker.component.PlayerDeckComponent
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer
import org.slf4j.LoggerFactory


object JokerMod : ModInitializer, EntityComponentInitializer {

	private val id: String = "joker"
    private val logger = LoggerFactory.getLogger(id)

	val MAGIK: ComponentKey<PlayerDeckComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(
		id("deck"),
		PlayerDeckComponent::class.java
	)


	override fun onInitialize() {

	}

	override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {

	}

	fun id(name: String) : ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(id, name)
	}
}