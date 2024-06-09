package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.networking.c2s.StartGameButtonPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class StartGameWidget(x: Int, y: Int, width: Int, height: Int, message: Component) : AbstractWidget(x, y, width, height,
    message
) {


    val SPRITES: WidgetSprites = WidgetSprites(
        ResourceLocation.withDefaultNamespace("widget/tab_selected"),
        ResourceLocation.withDefaultNamespace("widget/tab"),
        ResourceLocation.withDefaultNamespace("widget/tab_selected_highlighted"),
        ResourceLocation.withDefaultNamespace("widget/tab_highlighted")
    )


    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(
            SPRITES[false, this.isHoveredOrFocused],
            this.x,
            this.y,
            this.width,
            this.height
        )
        RenderSystem.disableBlend()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        println("Click")
        if (isValidClickButton(0)) {
            println("Click")
            ClientPlayNetworking.send(StartGameButtonPacket())
        }
    }
}