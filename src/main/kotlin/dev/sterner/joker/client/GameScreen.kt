package dev.sterner.joker.client

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.core.*
import dev.sterner.joker.game.CardEntity
import net.minecraft.client.GameNarrator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3f
import java.awt.Point


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var hand: MutableList<CardScreenObject>? = null
    val BACKGROUND_TEXTURE: ResourceLocation = JokerMod.id("textures/gui/balabg.png")
    protected var imageWidth: Int = 415
    protected var imageHeight: Int = 212

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player
        var obj = CardScreenObject()

        this.hand = makeHand()
    }

    fun makeHand(): MutableList<CardScreenObject> {
        val scaledY = (this.height - this.imageHeight) / 2
        val scaledX = (this.width - this.imageWidth) / 2

        //scaledY + this.imageHeight
        var list = mutableListOf<CardScreenObject>()


        var point = calculateEquallySpacedPoints(this.width + 150, 5)
        for (i in point) {
            //var entity = JokerMod.CARD_ENTITY.create(Minecraft.getInstance().level)
            list.add(makeCardScreenObject(Card(Suit.CLUBS, Rank.ACE, Special.NONE, Stamp.NONE), i, 50))
        }

        return list
    }

    fun makeCardScreenObject(card: Card, x: Int, y: Int): CardScreenObject {
        val obj = CardScreenObject()
        obj.card = card
        obj.centerPoint = Point(x, y)
        return obj
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
        val quaternionf = Quaternionf().rotateZ(Math.PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(1 * (Math.PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)
        var entity = JokerMod.CARD_ENTITY.create(Minecraft.getInstance().level)

        val scaledX = (this.width - this.imageWidth) / 2
        val scaledY = (this.height - this.imageHeight) / 2

        renderEntityInInventory(guiGraphics, mouseX.toFloat(), mouseY.toFloat(), 16f, Vector3f(1f,1f,1f), quaternionf, entity!!)
        for (card in hand!!) {

            var cardEntity: CardEntity = JokerMod.CARD_ENTITY.create(Minecraft.getInstance().level)!!
            cardEntity.card = card.card
            //var pos = Vector3f(card.centerPoint!!.x.toFloat(), card.centerPoint!!.y.toFloat(), 0f)
            renderEntityInInventory(guiGraphics, card.centerPoint!!.x.toFloat(), card.centerPoint!!.y.toFloat(), 16f, Vector3f(0f,0f,0f), quaternionf, cardEntity)
            println("${card.centerPoint!!.x.toFloat()}" + "${card.centerPoint!!.y.toFloat()}")
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

        fun renderEntityInInventory(
            guiGraphics: GuiGraphics,
            x: Float,
            y: Float,
            scale: Float,
            translate: Vector3f,
            pose: Quaternionf?,
            entity: Entity
        ) {
            guiGraphics.pose().pushPose()
            guiGraphics.pose().translate(x.toDouble(), y.toDouble(), 50.0)
            guiGraphics.pose().scale(scale, scale, -scale)
            guiGraphics.pose().translate(translate.x, translate.y, translate.z)
            guiGraphics.pose().mulPose(pose)
            Lighting.setupLevel()
            val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher

            entityRenderDispatcher.setRenderShadow(false)
            RenderSystem.runAsFancy {
                entityRenderDispatcher.render(
                    entity,
                    0.0,
                    0.0,
                    0.0,
                    0.0f,
                    1.0f,
                    guiGraphics.pose(),
                    guiGraphics.bufferSource() as MultiBufferSource,
                    0xF000F0
                )
            }
            guiGraphics.flush()
            entityRenderDispatcher.setRenderShadow(true)
            guiGraphics.pose().popPose()
            Lighting.setupFor3DItems()
        }
    }

}