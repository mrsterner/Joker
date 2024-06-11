package dev.sterner.joker.client.widget

import dev.sterner.joker.client.GameScreen
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

abstract class AbstractGameWidget(var screen: GameScreen, x: Int, y: Int, width: Int, height: Int) : AbstractWidget(x, y, width, height,
    Component.literal("")
) {

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }
}