package net.offhandswitcher;

import net.offhandswitcher.network.Networking;

public class OffHandSwitcherMod {
    public static final String MOD_ID = "offhandswitcher";

    public static void init() {
        Networking.commonInit();
    }
}
