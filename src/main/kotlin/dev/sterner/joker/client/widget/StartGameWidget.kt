package dev.sterner.joker.client.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class StartGameWidget(x: Int, y: Int, width: Int, height: Int, message: Component) : AbstractWidget(x, y, width, height,
    message
) {

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        TODO("Not yet implemented")
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseClicked(mouseX, mouseY, button)
    }
}