package dev.sterner.joker.client

import com.mojang.math.Axis
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.widget.StartGameWidget
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.core.*
import dev.sterner.joker.game.CardObject
import dev.sterner.joker.game.GameLoop
import net.minecraft.client.GameNarrator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.resolver.ServerAddress
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3i
import java.util.function.Consumer


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var gameLoop: GameLoop? = null

    private val BACKGROUND_TEXTURE: ResourceLocation = JokerMod.id("textures/gui/balabg.png")
    private var imageWidth: Int = 415
    private var imageHeight: Int = 212

    var draggingObject: CardObject? = null
    var offsetX = 0.0
    var offsetY = 0.0

    //From Component
    var deck: MutableList<Card>? = null

    var backside = CardObject()

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player

        val components = JokerComponents.DECK.get(player)
        this.deck = components.gameDeck
        this.gameLoop = components.gameLoop
        this.backside = CardObject()
    }

    override fun init() {
       this.addRenderableWidget(StartGameWidget(64 ,64 ,16, 16, Component.literal("Hello")))
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (minecraft!!.options.keyInventory.matches(keyCode, scanCode)) {
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            var highestZObject: CardObject? = null
            var highestZ = Int.MIN_VALUE

            for (obj in gameLoop!!.hand) {
                val x = obj.screenPos.x
                val y = obj.screenPos.y

                if (mouseX < x + (obj.width / 6) && mouseX > x - (obj.width)) {
                    if (mouseY < y + (obj.height / 6) && mouseY > y - (obj.height)) {
                        if (obj.screenPos.z > highestZ) {
                            highestZObject = obj
                            highestZ = obj.screenPos.z
                        }
                    }
                }
            }

            // If a card with the highest z-coordinate is found, set it as the dragging object
            if (highestZObject != null) {
                draggingObject = highestZObject
                draggingObject!!.isHolding = true
                offsetX = mouseX - highestZObject.screenPos.x
                offsetY = mouseY - highestZObject.screenPos.y
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0 && draggingObject != null) {
            draggingObject!!.screenPos = Vector3i((mouseX - offsetX).toInt(), this.height - gameLoop!!.handLevelY - 8, 200)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (draggingObject != null) {
            val vec = draggingObject!!.screenPos
            draggingObject!!.screenPos = Vector3i(vec.x, vec.y, 0)
            draggingObject!!.isHolding = false
            draggingObject = null
            gameLoop!!.reorderHand()
        }

        offsetX = 0.0
        offsetY = 0.0

        return super.mouseReleased(mouseX, mouseY, button)
    }



    fun orderHandByRank() {
        gameLoop!!.hand.sortBy { it.card.rank }
    }

    fun orderHandBySuit() {
        gameLoop!!.hand.sortBy { it.card.suit }
    }

    override fun tick() {
        super.tick()
        this.gameLoop = JokerComponents.DECK.get(player!!).gameLoop
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(1 * (Math.PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)

        for (cardObject in gameLoop!!.hand) {
            GameUtils.renderCard(guiGraphics, cardObject.screenPos, 16f, quaternionf, cardObject, partialTick)
        }
        quaternionf.mul(Axis.YP.rotationDegrees(180f))

        for (index in 1 until 5) {
            GameUtils.renderCard(guiGraphics, Vector3d(this.width - 50.0 + (index / 2),this.height - 40.0 + (index / 2), index.toDouble()), 16f, quaternionf, backside, partialTick)
        }

    }



    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderTransparentBackground(guiGraphics)
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }

    fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val scaledX = (this.width - this.imageWidth) / 2
        val scaledY = (this.height - this.imageHeight) / 2

        //Draw transparent line on top of gui, and bottom
        guiGraphics.fillGradient(0, scaledY - 3, this.width, scaledY, -1072689136, -804253680)
        guiGraphics.fillGradient(0, scaledY + this.imageHeight, this.width, scaledY + this.imageHeight + 3, -1072689136, -804253680)

        //Draw texture
        guiGraphics.blit(
            this.BACKGROUND_TEXTURE, scaledX, scaledY, 0, 0.0f, 0.0f,
            this.imageWidth,
            this.imageHeight, 512, 256
        )
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