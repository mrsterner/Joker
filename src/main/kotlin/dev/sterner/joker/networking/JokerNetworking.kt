package dev.sterner.joker.networking

import dev.sterner.joker.JokerMod
import dev.sterner.joker.component.JokerComponents
import dev.sterner.joker.networking.c2s.StartGameButtonPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


object JokerNetworking {
    fun init(){
        JokerMod.logInitialization()



    }
}