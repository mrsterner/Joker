package dev.sterner.joker

import com.mojang.blaze3d.platform.InputConstants
import dev.sterner.joker.client.GameScreen
import dev.sterner.joker.client.model.CardEntityModel
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.networking.JokerNetworking
import dev.sterner.joker.networking.c2s.StartGameButtonPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory


object JokerMod : ModInitializer, ClientModInitializer {

	val id: String = "joker"
	val logger = LoggerFactory.getLogger(id)
	var keyBinding: KeyMapping? = null


	override fun onInitialize() {
		JokerNetworking.init()
	}

	override fun onInitializeClient() {

		EntityModelLayerRegistry.registerModelLayer(CardEntityModel.LAYER_LOCATION, CardEntityModel.Companion::createCardOverlayModel)

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

		PayloadTypeRegistry.playS2C().register(StartGameButtonPacket.TYPE, StartGameButtonPacket.STREAM_CODEC)
		PayloadTypeRegistry.playC2S().register(StartGameButtonPacket.TYPE, StartGameButtonPacket.STREAM_CODEC)


		ServerPlayNetworking.registerGlobalReceiver(StartGameButtonPacket.TYPE) { packet, ctx ->
			ctx.player().server.execute {

				val component = JokerComponents.DECK.get(ctx.player())
				component.gameOn = !component.gameOn
				if (!component.gameOn ) {
					component.gameDeck.clear()
					component.hand.clear()
				}
				println("Game status: ${component.gameOn}")
				JokerComponents.DECK.sync(ctx.player())
			}
		}
	}

	fun id(name: String) : ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(id, name)
	}

	fun logInitialization() {
		val logger = LoggerFactory.getLogger(this::class.java)
		logger.info("${this::class.java.simpleName} initialized")
	}
}