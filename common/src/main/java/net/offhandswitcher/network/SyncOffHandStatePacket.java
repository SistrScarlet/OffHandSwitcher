package net.offhandswitcher.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SyncOffHandStatePacket {

    @ExpectPlatform
    public static void sendC2S(int offhandSlot, boolean switchOffhand) {
        throw new AssertionError();
    }

}
