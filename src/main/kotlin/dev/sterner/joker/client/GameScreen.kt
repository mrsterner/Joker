package dev.sterner.joker.client

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.joker.JokerMod
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.core.Card
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


class GameScreen(component: Component) : Screen(component) {

    var player: Player? = null
    var deck: MutableList<Card>? = null
    val BACKGROUND_TEXTURE: ResourceLocation = JokerMod.id("textures/gui/balabg.png")
    protected var imageWidth: Int = 415
    protected var imageHeight: Int = 212

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player
        this.deck = JokerComponents.DECK.get(player).deck
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

        renderEntityInInventory(guiGraphics, mouseX.toFloat(), mouseY.toFloat(), 16f, Vector3f(1f,1f,1f), quaternionf, quaternionf2, entity!!)

    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderTransparentBackground(guiGraphics)
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY)
    }

    fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val i = (this.width - this.imageWidth) / 2
        val j = (this.height - this.imageHeight) / 2

        //Draw transparent line on top of gui, and bottom
        guiGraphics.fillGradient(0, j - 3, this.width, j, -1072689136, -804253680)
        guiGraphics.fillGradient(0, j + this.imageHeight, this.width, j + this.imageHeight + 3, -1072689136, -804253680)

        //Draw texture
        guiGraphics.blit(
            this.BACKGROUND_TEXTURE, i, j, 0, 0.0f, 0.0f,
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
            cameraOrientation: Quaternionf?,
            entity: Entity
        ) {
            guiGraphics.pose().pushPose()
            guiGraphics.pose().translate(x.toDouble(), y.toDouble(), 50.0)
            guiGraphics.pose().scale(scale, scale, -scale)
            guiGraphics.pose().translate(translate.x, translate.y, translate.z)
            guiGraphics.pose().mulPose(pose)
            Lighting.setupLevel()
            val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
            if (cameraOrientation != null) {
                entityRenderDispatcher.overrideCameraOrientation(
                    cameraOrientation.conjugate(Quaternionf()).rotateY(Math.PI.toFloat())
                )
            }
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