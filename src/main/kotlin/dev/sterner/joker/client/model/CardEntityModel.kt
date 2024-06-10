package dev.sterner.joker.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.joker.JokerMod
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function


class CardEntityModel(val root: ModelPart) : Model(Function { location: ResourceLocation -> RenderType.entitySolid(location) }) {

    override fun renderToBuffer(poseStack: PoseStack, vertexConsumer: VertexConsumer, packedLight: Int, packedOverlay: Int, k: Int) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, k)
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(JokerMod.id("card"), "main")

        fun createCardOverlayModel(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partdefinition = meshDefinition.root

            partdefinition.addOrReplaceChild("root",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-15.5f, -22.5f, -0.5f, 31.0f, 45.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }
}