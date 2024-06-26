package net.offhandswitcher.forge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.offhandswitcher.OffHandSwitcherMod;
import net.offhandswitcher.client.OffHandSwitcherModClient;

@Mod(OffHandSwitcherMod.MOD_ID)
public class OffHandSwitcherModForge {
    public OffHandSwitcherModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(OffHandSwitcherMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OffHandSwitcherMod.init();
        if (Platform.getEnv() == Dist.CLIENT) {
            OffHandSwitcherModClient.init();
        }
    }
}
