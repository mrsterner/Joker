package dev.sterner.joker.client

import dev.sterner.joker.core.Card
import net.minecraft.client.GameNarrator
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var deck: List<Card>? = null

    constructor(Player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (minecraft!!.options.keyInventory.matches(keyCode, scanCode)) {
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0) {
            if (tab != null) {
                return tab.move(mouseX, mouseY, button, deltaX, deltaY)
            }
        }
        return false
    }

    override fun tick() {
        super.tick()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}