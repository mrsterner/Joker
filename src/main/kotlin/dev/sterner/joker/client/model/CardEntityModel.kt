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


class CardEntityModel<T : Entity>(root: ModelPart) : EntityModel<T>() {

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

            partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-15.5f, -22.5f, -0.5f, 31.0f, 45.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }
}