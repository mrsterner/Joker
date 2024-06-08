package dev.sterner.joker.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.joker.JokerMod
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.entity.Entity


class CardEntityOverlayModel<T : Entity>(root: ModelPart) : EntityModel<T>() {

    private val bbMain: ModelPart = root.getChild("bb_main")

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        // Animation logic goes here
    }

    override fun renderToBuffer(poseStack: PoseStack, vertexConsumer: VertexConsumer, packedLight: Int, packedOverlay: Int, k: Int) {
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, k)
    }


    companion object {
        val LAYER_LOCATION = ModelLayerLocation(JokerMod.id("card_overlay"), "main")

        fun createCardOverlayModel(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partdefinition = meshDefinition.root

            var bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-15.5F, -22.5F, -0.5F, 31.0F, 45.0F, 0.0F, CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
            return LayerDefinition.create(meshDefinition, 128, 128)
        }
    }
}