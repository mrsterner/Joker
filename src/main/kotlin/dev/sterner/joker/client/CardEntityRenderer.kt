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

    var model: EntityModel<CardEntity>? = null
    var overlay_model: EntityModel<CardEntity>? = null

    init {
        model = CardEntityModel(context.bakeLayer(CardEntityModel.LAYER_LOCATION))
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


        val i: Float = (gameTime + partialTick) / 500f
        poseStack.mulPose(Axis.YP.rotation(i))

        model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.translate(0f,0f,-0.01f)
        overlay_model?.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(JokerMod.id("textures/card/card_overlay.png"))), packedLight, OverlayTexture.NO_OVERLAY)

        poseStack.popPose()
     }

    override fun getTextureLocation(entity: CardEntity): ResourceLocation {
        return JokerMod.id("textures/card/texture.png")
    }
}