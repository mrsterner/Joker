package dev.sterner.joker.client

import dev.sterner.joker.JokerMod
import dev.sterner.joker.core.*
import net.minecraft.client.GameNarrator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector3i
import java.awt.Point


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var hand: MutableList<CardScreenObject>? = null
    val BACKGROUND_TEXTURE: ResourceLocation = JokerMod.id("textures/gui/balabg.png")
    protected var imageWidth: Int = 415
    protected var imageHeight: Int = 212

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player

    }

    override fun init() {
        super.init()
        this.minecraft = Minecraft.getInstance()
        this.width = minecraft!!.window.getGuiScaledWidth()
        this.height = minecraft!!.window.getGuiScaledHeight()
        this.hand = makeHand()
    }

    fun makeHand(): MutableList<CardScreenObject> {
        val list = mutableListOf<CardScreenObject>()

        val point = calculateEquallySpacedPoints(this.width / 2, 5)
        for (i in point) {
            val pos = Vector3i(48 + i, this.height - 100, 0)
            list.add(makeCardScreenObject(Card(Suit.entries.random(), Rank.entries.random(), Special.NONE, Stamp.NONE), pos))
        }

        return list
    }

    fun makeCardScreenObject(card: Card, pos: Vector3i): CardScreenObject {
        val obj = CardScreenObject()
        val entity = JokerMod.CARD_ENTITY.create(Minecraft.getInstance().level!!)
        entity!!.card = card
        obj.card = entity
        obj.centerPoint = pos
        return obj
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (minecraft!!.options.keyInventory.matches(keyCode, scanCode)) {
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    var draggingObject: CardScreenObject? = null
    var offsetX = 0.0
    var offsetY = 0.0

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            for (obj in hand!!) {
                val x = obj.centerPoint!!.x
                val y = obj.centerPoint!!.y

                if (mouseX < x + (obj.width!! / 4) && mouseX > x - (obj.width!!)) {
                    if (mouseY < y + (obj.height / 4) && mouseY > y - (obj.height)) {
                        draggingObject = obj
                        offsetX = mouseX - x
                        offsetY = mouseY - y
                        break
                    }
                }
            }
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0 && draggingObject != null) {
            val z = draggingObject!!.centerPoint.z
            draggingObject!!.centerPoint = Vector3i((mouseX - offsetX).toInt(), minecraft!!.window.getGuiScaledHeight() - 100, 10)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        var vec = draggingObject!!.centerPoint
        draggingObject!!.centerPoint = Vector3i(vec.x, vec.y, 0)
        draggingObject = null
        offsetX = 0.0
        offsetY = 0.0
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun tick() {
        super.tick()

    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(1 * (Math.PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)

        for (card in hand!!) {
            GameUtils.renderCard(guiGraphics, card.centerPoint, 16f, quaternionf, card.card!!, partialTick)
        }
    }

    fun calculateEquallySpacedPoints(width: Int, n: Int): List<Int> {
        if (n <= 1) throw IllegalArgumentException("Number of points must be greater than 1")

        val spacing = width / (n - 1)

        val points = mutableListOf<Int>()
        for (i in 0 until n) {
            points.add(i * spacing)
        }

        return points
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