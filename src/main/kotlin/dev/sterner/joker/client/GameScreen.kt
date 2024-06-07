package dev.sterner.joker.client

import dev.sterner.joker.JokerMod
import dev.sterner.joker.core.Card
import net.minecraft.client.GameNarrator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var deck: MutableList<Card>? = null

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player
        this.deck = JokerMod.DECK.get(player).deck
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

        }
        return false
    }

    override fun tick() {
        super.tick()

    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    companion object{
        var screen: GameScreen? = null

        fun openScreen(player: Player) {
            Minecraft.getInstance().setScreen(getInstance(player))
        }

        fun getInstance(player: Player): GameScreen {
            if (screen == null) {
                screen = GameScreen(player)
            }
            return screen!!
        }
    }

}