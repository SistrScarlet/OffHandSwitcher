package net.offhandswitcher.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.offhandswitcher.network.Networking;

@Environment(EnvType.CLIENT)
public class OffHandSwitcherModClient {

    public static void init() {
        Networking.clientInit();
    }

}
