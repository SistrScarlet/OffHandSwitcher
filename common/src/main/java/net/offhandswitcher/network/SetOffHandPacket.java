package net.offhandswitcher.network;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SetOffHandPacket {

    @ExpectPlatform
    public static void sendC2S(int from, int to) {
        throw new AssertionError();
    }

}
