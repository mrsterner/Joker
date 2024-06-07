package dev.sterner.joker.client

import dev.sterner.joker.game.CardEntity
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class CardEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<CardEntity>(context) {



    override fun getTextureLocation(entity: CardEntity): ResourceLocation {
        TODO("Not yet implemented")
    }
}