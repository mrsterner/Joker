package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.GameScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component

class RunInfoWidget(screen: GameScreen, x: Int, y: Int, width: Int, height: Int) : AbstractGameWidget(
    screen, x, y, width, height
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        JokerMod.id("widget/run_info_selected"),
        JokerMod.id("widget/run_info"),
        JokerMod.id("widget/run_info_selected_highlighted"),
        JokerMod.id("widget/run_info_highlighted")
    )

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        guiGraphics.pose().pushPose()
        guiGraphics.pose().scale(0.58f, 0.58f, 1.0f)
        guiGraphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.literal("Run Info"),
            (this.x * 1.75).toInt() + width - 4,
            (this.y * 1.75).toInt() + height - 9,
            0xFFFFFF
        )
        //1.5 = 0.66
        //1.75 = 0.58
        //2.0 = 0.50
        guiGraphics.pose().popPose()
        RenderSystem.disableBlend()
    }

    override fun onClick(mouseX: Double, mouseY: Double) {


        super.onClick(mouseX, mouseY)
    }
}