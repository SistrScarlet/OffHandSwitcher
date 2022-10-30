package net.offhandswitcher.fabric;

import net.offhandswitcher.OffHandSwitcherMod;
import net.fabricmc.api.ModInitializer;

public class OffHandSwitcherModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OffHandSwitcherMod.init();
    }
}
