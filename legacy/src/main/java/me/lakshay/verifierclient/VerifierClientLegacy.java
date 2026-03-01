package me.lakshay.verifierclient;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public class VerifierClientLegacy implements ClientModInitializer {

    // Must match your Paper plugin channel EXACTLY
    private static final Identifier CHANNEL = new Identifier("lakshay", "verify");

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL, (client, handler, buf, responseSender) -> {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            String msg = new String(data, StandardCharsets.UTF_8);

            if (!"REQ".equals(msg)) return;

            String payload = Common.buildModsPayload();

            PacketByteBuf out = new PacketByteBuf(Unpooled.buffer());
            out.writeBytes(payload.getBytes(StandardCharsets.UTF_8));

            ClientPlayNetworking.send(CHANNEL, out);
        });
    }
}
