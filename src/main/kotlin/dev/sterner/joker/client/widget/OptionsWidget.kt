package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.GameScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component

class OptionsWidget(screen: GameScreen, x: Int, y: Int, width: Int, height: Int) : AbstractGameWidget(
    screen, x, y, width, height
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        JokerMod.id("widget/options_selected"),
        JokerMod.id("widget/options"),
        JokerMod.id("widget/options_selected_highlighted"),
        JokerMod.id("widget/options_highlighted")
    )

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        guiGraphics.pose().pushPose()
        guiGraphics.pose().scale(0.66f, 0.66f, 1.0f)
        guiGraphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.literal("Options"),
            (this.x * 1.5).toInt() + width - 6,
            (this.y * 1.5).toInt() + height - 6,
            0xFFFFFF
        )
        guiGraphics.pose().popPose()
        RenderSystem.disableBlend()
    }

    override fun onClick(mouseX: Double, mouseY: Double) {


        super.onClick(mouseX, mouseY)
    }
}