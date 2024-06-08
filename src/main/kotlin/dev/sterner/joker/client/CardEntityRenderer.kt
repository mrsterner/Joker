package dev.sterner.joker.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.joker.JokerMod
import dev.sterner.joker.client.model.CardEntityModel
import dev.sterner.joker.client.model.CardEntityOverlayModel
import dev.sterner.joker.game.CardEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class CardEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<CardEntity>(context) {

    //var model: EntityModel<CardEntity>? = null
    var overlay_model: EntityModel<CardEntity>? = null

    init {
        //model = CardEntityModel(context.bakeLayer(CardEntityModel.LAYER_LOCATION))
        overlay_model = CardEntityOverlayModel(context.bakeLayer(CardEntityOverlayModel.LAYER_LOCATION))
    }

    override fun render(
        entity: CardEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)

        poseStack.pushPose()
        val gameTime = Minecraft.getInstance().level!!.gameTime


        val i: Float = (gameTime + partialTick) / 20f

        poseStack.translate((31 / 16f) / 2f,(45f / 16f) / 1.15f,0f)
        poseStack.mulPose(Axis.YP.rotation(i))
        poseStack.scale(-1.0f, -1.0f, 1.0f)

        renderCard(entity, poseStack, buffer, partialTick, packedLight)

        poseStack.popPose()
     }

    fun renderCard(entity: CardEntity, poseStack: PoseStack, buffer: MultiBufferSource, partialTick: Float, packedLight: Int){
        overlay_model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(getBackTexture(entity))), packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.translate(0f,0f,0.01f)
        overlay_model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(getCardBackTexture(entity))), packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.translate(0f,0f,0.01f)
        overlay_model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(getCardFrontTexture(entity))), packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.translate(0f,0f,0.01f)
        overlay_model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(getFaceTexture(entity))), packedLight, OverlayTexture.NO_OVERLAY)
    }

    override fun getTextureLocation(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/base.png")
    }

    fun getFaceTexture(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/ace_of_hearts.png")
    }

    fun getCardFrontTexture(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/base.png")
    }

    fun getCardBackTexture(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/back.png")
    }

    fun getBackTexture(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/back_overlay.png")
    }
}