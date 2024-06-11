package dev.sterner.joker.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.model.CardEntityModel
import dev.sterner.joker.game.CardObject
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

object CardRenderer {

    var overlay_model: CardEntityModel? = null

    init {
        var modelLayer: ModelPart = Minecraft.getInstance().entityModels.bakeLayer(CardEntityModel.LAYER_LOCATION)
        overlay_model = CardEntityModel(modelLayer)
    }

    fun renderCard(
        cardObject: CardObject,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        partialTick: Float,
        packedLight: Int
    ) {

        poseStack.pushPose()
        val gameTime = Minecraft.getInstance().level!!.gameTime


        val i: Float = (gameTime + partialTick) / 20f

        poseStack.translate((31 / 16f) / 2f, (45f / 16f) / 1.15f, 0f)
        //poseStack.mulPose(Axis.YP.rotation(i))
        poseStack.mulPose(Axis.YP.rotationDegrees(180f))
        poseStack.scale(1.0f, -1.0f, 1.0f)

        overlay_model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(getBackOverlayTexture(cardObject))),
            packedLight,
            OverlayTexture.NO_OVERLAY
        )
        poseStack.translate(0f, 0f, 0.01f)
        overlay_model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(getCardBackTexture(cardObject))),
            packedLight,
            OverlayTexture.NO_OVERLAY
        )
        poseStack.translate(0f, 0f, 0.01f)
        overlay_model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(getCardFrontTexture(cardObject))),
            packedLight,
            OverlayTexture.NO_OVERLAY
        )
        poseStack.translate(0f, 0f, 0.01f)
        overlay_model?.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(getFrontOverlayTexture(cardObject))),
            packedLight,
            OverlayTexture.NO_OVERLAY
        )

        poseStack.popPose()


    }

    fun getFrontOverlayTexture(cardObject: CardObject): ResourceLocation {
        if (cardObject.card != null) {
            return JokerMod.id("textures/card/front_overlay/" + cardObject.getCardName(cardObject.card) + ".png")
        }
        return JokerMod.id("textures/card/front_overlay/ace_of_spades.png")
    }

    fun getCardFrontTexture(cardObject: CardObject): ResourceLocation {
        if (cardObject.card != null) {
            //NONE, WILD, LUCKY, STEEL, GOLD
            return JokerMod.id("textures/card/base/" + cardObject.getBaseName(cardObject.card) + ".png")
        }
        return JokerMod.id("textures/card/base/base.png")
    }

    fun getCardBackTexture(cardObject: CardObject): ResourceLocation {
        return JokerMod.id("textures/card/base/base.png")
    }

    fun getBackOverlayTexture(cardObject: CardObject): ResourceLocation {
        return JokerMod.id("textures/card/back_overlay/blue_overlay.png")
    }
}