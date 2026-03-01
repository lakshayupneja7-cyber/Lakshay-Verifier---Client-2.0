package me.lakshay.verifierclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public class VerifierClientModern implements ClientModInitializer {

    public static final CustomPayload.Id<VerifyPayload> ID =
            new CustomPayload.Id<>(Identifier.of("lakshay", "verify"));

    // Paper sends RAW bytes ("REQ"), so codec reads/writes raw bytes only
    public static final PacketCodec<RegistryByteBuf, VerifyPayload> CODEC =
            PacketCodec.ofStatic(
                    (buf, payload) -> buf.writeBytes(payload.data.getBytes(StandardCharsets.UTF_8)),
                    buf -> {
                        byte[] data = new byte[buf.readableBytes()];
                        buf.readBytes(data);
                        return new VerifyPayload(new String(data, StandardCharsets.UTF_8));
                    }
            );

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
        PayloadTypeRegistry.playC2S().register(ID, CODEC);

        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            if (!"REQ".equals(payload.data)) return;
            ClientPlayNetworking.send(new VerifyPayload(Common.buildModsPayload()));
        });
    }

    public record VerifyPayload(String data) implements CustomPayload {
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
