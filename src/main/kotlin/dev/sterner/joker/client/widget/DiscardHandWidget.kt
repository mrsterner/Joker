package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class DiscardHandWidget(x: Int, y: Int, width: Int, height: Int, message: Component) : AbstractWidget(x, y, width, height,
    message
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        JokerMod.id("widget/discard_hand_selected"),
        JokerMod.id("widget/discard_hand"),
        JokerMod.id("widget/discard_hand_selected_highlighted"),
        JokerMod.id("widget/discard_hand_highlighted")
    )

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        RenderSystem.disableBlend()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        //TODO Open Options Screen
        super.onClick(mouseX, mouseY)
    }
}