package dev.sterner.joker.networking.c2s

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation


class StartGameButtonPacket() : CustomPacketPayload {

    companion object {
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, StartGameButtonPacket> = CustomPacketPayload.codec(
            { value, buffer -> value.write(buffer) },
            { buffer -> StartGameButtonPacket(buffer) }
        )

        val TYPE: CustomPacketPayload.Type<StartGameButtonPacket> = CustomPacketPayload.Type<StartGameButtonPacket>(
            ResourceLocation.tryBuild("joker", "start_game")!!
        )

    }

    constructor(buffer: FriendlyByteBuf) : this(

    )

    private fun write(buffer: FriendlyByteBuf) {

    }

    override fun type(): CustomPacketPayload.Type<StartGameButtonPacket> {
        return TYPE
    }
}