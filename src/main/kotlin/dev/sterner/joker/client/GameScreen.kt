package dev.sterner.joker.client

import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.widget.StartGameWidget
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.core.*
import dev.sterner.joker.game.GameLoop
import dev.sterner.joker.networking.c2s.StartGameButtonPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.GameNarrator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3i


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var gameLoop: GameLoop? = null

    private val BACKGROUND_TEXTURE: ResourceLocation = JokerMod.id("textures/gui/balabg.png")
    private var imageWidth: Int = 415
    private var imageHeight: Int = 212

    var draggingObject: CardScreenObject? = null
    var offsetX = 0.0
    var offsetY = 0.0



    //From Component
    var handSize = 8
    var deck: MutableList<Card>? = null

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player

        val components = JokerComponents.DECK.get(player)
        this.handSize = components.handSize
        this.deck = components.gameDeck()
        this.gameLoop = components.gameLoop
    }

    override fun init() {
        //TODO obviously remove this
        //this.hand = makeHand()
        val scaledX = (this.width - this.imageWidth) / 2
        val scaledY = (this.height - this.imageHeight) / 2

        val wig = StartGameWidget(64, 64, 18, 18, Component.literal("Hello"))

        this.addRenderableWidget(wig)
    }

    /*
    private fun makeHand(): MutableList<CardScreenObject> {
        val list = mutableListOf<CardScreenObject>()

        val point = calculateEquallySpacedPoints()
        for ((j, i) in point.withIndex()) {

            val pos = Vector3i(handLevelX + i, this.height - handLevelY, j)
            list.add(GameUtils.makeCardScreenObject(Card(Suit.entries.random(), Rank.entries.random(), Special.NONE, Stamp.NONE), pos))
        }

        return list
    }

     */

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (minecraft!!.options.keyInventory.matches(keyCode, scanCode)) {
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            var highestZObject: CardScreenObject? = null
            var highestZ = Int.MIN_VALUE

            for (obj in gameLoop!!.hand!!) {
                val x = obj.centerPos.x
                val y = obj.centerPos.y

                if (mouseX < x + (obj.width!! / 6) && mouseX > x - (obj.width!!)) {
                    if (mouseY < y + (obj.height / 6) && mouseY > y - (obj.height)) {
                        if (obj.centerPos.z > highestZ) {
                            highestZObject = obj
                            highestZ = obj.centerPos.z
                        }
                    }
                }
            }

            // If a card with the highest z-coordinate is found, set it as the dragging object
            if (highestZObject != null) {
                draggingObject = highestZObject
                offsetX = mouseX - highestZObject.centerPos.x
                offsetY = mouseY - highestZObject.centerPos.y
            }
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0 && draggingObject != null) {
            draggingObject!!.centerPos = Vector3i((mouseX - offsetX).toInt(), this.height - gameLoop!!.handLevelY, 10)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (draggingObject != null) {
            val vec = draggingObject!!.centerPos
            draggingObject!!.centerPos = Vector3i(vec.x, vec.y, 0)
            draggingObject = null
        }

        offsetX = 0.0
        offsetY = 0.0
        gameLoop!!.reorderHand()
        return super.mouseReleased(mouseX, mouseY, button)
    }



    fun orderHandByRank() {
        gameLoop!!.hand?.sortBy { it.cardEntity?.card?.rank }
    }

    fun orderHandBySuit() {
        gameLoop!!.hand?.sortBy { it.cardEntity?.card?.suit }
    }

    override fun tick() {
        super.tick()

    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(1 * (Math.PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)

        for (cardObject in gameLoop!!.hand!!) {
            GameUtils.renderCard(guiGraphics, cardObject.centerPos, 16f, quaternionf, cardObject.cardEntity!!, partialTick)
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