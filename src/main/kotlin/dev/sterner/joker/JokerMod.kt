package dev.sterner.joker

import dev.sterner.joker.client.CardEntityRenderer
import dev.sterner.joker.component.PlayerDeckComponent
import dev.sterner.joker.game.CardEntity
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer
import org.slf4j.LoggerFactory


object JokerMod : ModInitializer, ClientModInitializer, EntityComponentInitializer {

	private val id: String = "joker"
    private val logger = LoggerFactory.getLogger(id)

	val DECK: ComponentKey<PlayerDeckComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(
		id("deck"),
		PlayerDeckComponent::class.java
	)

	val CARD_ENTITY = EntityType.Builder.of(::CardEntity, MobCategory.MISC).build("card_entity")


	override fun onInitialize() {
		Registry.register(BuiltInRegistries.ENTITY_TYPE, "card_entity", CARD_ENTITY)
	}

	override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {

	}

	fun id(name: String) : ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(id, name)
	}

	override fun onInitializeClient() {
		EntityRendererRegistry.register(CARD_ENTITY, ::CardEntityRenderer)

	}
}