package net.offhandswitcher.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.offhandswitcher.OffHandSwitcherMod;
import net.offhandswitcher.client.OffHandSwitcherModClient;

public class OffHandSwitcherModFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        OffHandSwitcherMod.init();
    }

    @Override
    public void onInitializeClient() {
        OffHandSwitcherModClient.init();
    }
}
