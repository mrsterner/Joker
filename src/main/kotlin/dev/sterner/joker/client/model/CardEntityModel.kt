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

            var bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(1, 1).addBox(-14.5F, -21.5F, 0.0F, 29.0F, 43.0F, 0.0F, CubeDeformation(0.0F))
                .texOffs(5, 49).addBox(14.5F, -20.5F, 0.0F, 1.0F, 41.0F, 0.0F, CubeDeformation(0.0F))
                .texOffs(1, 49).addBox(-15.5F, -20.5F, 0.0F, 1.0F, 41.0F, 0.0F, CubeDeformation(0.0F))
                .texOffs(1, 47).addBox(-13.5F, 21.5F, 0.0F, 27.0F, 1.0F, 0.0F, CubeDeformation(0.0F))
                .texOffs(1, 45).addBox(-13.5F, -22.5F, 0.0F, 27.0F, 1.0F, 0.0F, CubeDeformation(0.0F)), PartPose.offset(-14.5F, 1.5F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}