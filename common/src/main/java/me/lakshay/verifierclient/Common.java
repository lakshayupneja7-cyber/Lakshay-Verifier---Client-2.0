package me.lakshay.verifierclient;

import net.fabricmc.loader.api.FabricLoader;

import java.util.stream.Collectors;

public final class Common {
    private Common() {}

    public static String buildModsPayload() {
        String mods = FabricLoader.getInstance().getAllMods().stream()
                .map(m -> m.getMetadata().getId())
                .collect(Collectors.joining(","));
        return "MODS|" + mods;
    }
}
