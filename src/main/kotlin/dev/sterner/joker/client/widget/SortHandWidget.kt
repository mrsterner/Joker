package dev.sterner.joker.client.widget

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.GameScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites

class SortHandWidget(screen: GameScreen, val rank: Boolean, x: Int, y: Int, width: Int, height: Int) : AbstractGameWidget(
    screen, x, y, width, height
) {

    val SPRITES: WidgetSprites = WidgetSprites(
        JokerMod.id("widget/sort_hand_selected"),
        JokerMod.id("widget/sort_hand"),
        JokerMod.id("widget/sort_hand_selected_highlighted"),
        JokerMod.id("widget/sort_hand_highlighted")
    )

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        guiGraphics.blitSprite(SPRITES[false, this.isHoveredOrFocused], this.x, this.y, this.width, this.height)
        RenderSystem.disableBlend()
    }


    override fun onClick(mouseX: Double, mouseY: Double) {
        if (rank) {
            screen.gameLoop?.orderByRank = true
        } else {
            screen.gameLoop?.orderByRank = false
        }
        screen.gameLoop?.reorderHandByRankOrSuit()
        //TODO Open Options Screen
        super.onClick(mouseX, mouseY)
    }
}