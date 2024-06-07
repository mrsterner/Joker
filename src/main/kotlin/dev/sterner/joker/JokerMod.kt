package dev.sterner.joker

import com.mojang.blaze3d.platform.InputConstants
import dev.sterner.joker.client.CardEntityRenderer
import dev.sterner.joker.client.GameScreen
import dev.sterner.joker.client.model.CardEntityModel
import dev.sterner.joker.client.model.CardEntityOverlayModel
import dev.sterner.joker.game.CardEntity
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory


object JokerMod : ModInitializer, ClientModInitializer {

	val id: String = "joker"
	val logger = LoggerFactory.getLogger(id)
	var keyBinding: KeyMapping? = null

	val CARD_ENTITY = EntityType.Builder.of(::CardEntity, MobCategory.MISC).build("card_entity")

	override fun onInitialize() {
		Registry.register(BuiltInRegistries.ENTITY_TYPE, "card_entity", CARD_ENTITY)
	}

	fun id(name: String) : ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(id, name)
	}

	override fun onInitializeClient() {
		EntityRendererRegistry.register(CARD_ENTITY, ::CardEntityRenderer)

		EntityModelLayerRegistry.registerModelLayer(CardEntityModel.LAYER_LOCATION, CardEntityModel.Companion::createCardModel)
		EntityModelLayerRegistry.registerModelLayer(CardEntityOverlayModel.LAYER_LOCATION, CardEntityOverlayModel.Companion::createCardOverlayModel)

		keyBinding = KeyBindingHelper.registerKeyBinding(
			KeyMapping(
				"key.joker.open",  // The translation key of the keybinding's name
				InputConstants.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_R,  // The keycode of the key
				"category.joker.test" // The translation key of the keybinding's category.
			)
		)

		ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
			while (keyBinding!!.isDown) {
				client.player!!.sendSystemMessage(Component.literal("Key"))
				GameScreen.openScreen(client.player!!)
			}
		})
	}
}