package dev.sterner.joker.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.joker.JokerMod
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.Entity

class CardEntityModel<T : Entity>(root: ModelPart) : EntityModel<T>() {

    private val bone: ModelPart = root.getChild("bone")

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
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay)
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(JokerMod.id("card"), "main")

        fun createCardModel(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-29.0f, -44.0f, -1.0f, 29.0f, 43.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(4, 48)
                    .addBox(0.0f, -43.0f, -1.0f, 1.0f, 41.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 48)
                    .addBox(-30.0f, -43.0f, -1.0f, 1.0f, 41.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 46)
                    .addBox(-28.0f, -1.0f, -1.0f, 27.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 44)
                    .addBox(-28.0f, -45.0f, -1.0f, 27.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}