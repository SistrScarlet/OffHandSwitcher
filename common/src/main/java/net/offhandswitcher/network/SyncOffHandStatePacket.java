package net.offhandswitcher.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncOffHandStatePacket {

    @ExpectPlatform
    public static void sendC2S(int offhandSlot, boolean switchOffhand) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendS2C(ServerPlayerEntity player, int offhandSlot, boolean switchOffhand) {
        throw new AssertionError();
    }

}
