package me.lakshay.verifierclient;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.stream.Collectors;

public class VerifierClient implements ClientModInitializer {

    // ✅ Works on old versions (1.16/1.18/1.19/1.20.4)
    private static final Identifier VERIFY_REQUEST  = new Identifier("lakshay:verify_request");
    private static final Identifier VERIFY_RESPONSE = new Identifier("lakshay:verify_response");

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                VERIFY_REQUEST,
                (client, handler, buf, responseSender) -> {

                    String mods = FabricLoader.getInstance()
                            .getAllMods()
                            .stream()
                            .map(m -> m.getMetadata().getId())
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(","));

                    PacketByteBuf out = new PacketByteBuf(Unpooled.buffer());
                    out.writeString(mods);

                    // ✅ old API send signature
                    ClientPlayNetworking.send(VERIFY_RESPONSE, out);
                }
        );
    }
}
