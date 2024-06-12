package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.GameScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component

class PlayHandWidget(screen: GameScreen, x: Int, y: Int, width: Int, height: Int) : AbstractGameWidget(
    screen, x, y, width, height
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        JokerMod.id("widget/play_hand_selected"),
        JokerMod.id("widget/play_hand"),
        JokerMod.id("widget/play_hand_selected_highlighted"),
        JokerMod.id("widget/play_hand_highlighted")
    )

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        guiGraphics.pose().pushPose()
        guiGraphics.pose().scale(0.58f, 0.58f, 1.0f)
        guiGraphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.literal("Play Hand"),
            (this.x * 1.75).toInt() + width - 8,
            (this.y * 1.75).toInt() + height - 12,
            0xFFFFFF
        )
        guiGraphics.pose().popPose()
        RenderSystem.disableBlend()
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        screen.gameLoop?.isPlaying = true
        super.onClick(mouseX, mouseY)
    }
}