package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.client.GameScreen
import dev.sterner.joker.networking.c2s.StartGameButtonPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.resources.ResourceLocation

class StartGameWidget(screen: GameScreen, x: Int, y: Int, width: Int, height: Int) : AbstractGameWidget(
    screen, x, y, width, height
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        ResourceLocation.withDefaultNamespace("widget/tab_selected"),
        ResourceLocation.withDefaultNamespace("widget/tab"),
        ResourceLocation.withDefaultNamespace("widget/tab_selected_highlighted"),
        ResourceLocation.withDefaultNamespace("widget/tab_highlighted")
    )


    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        RenderSystem.disableBlend()
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        ClientPlayNetworking.send(StartGameButtonPacket())
        super.onClick(mouseX, mouseY)
    }
}