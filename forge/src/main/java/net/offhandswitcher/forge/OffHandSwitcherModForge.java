package net.offhandswitcher.forge;

import dev.architectury.platform.forge.EventBuses;
import net.offhandswitcher.OffHandSwitcherMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OffHandSwitcherMod.MOD_ID)
public class OffHandSwitcherModForge {
    public OffHandSwitcherModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(OffHandSwitcherMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OffHandSwitcherMod.init();
    }
}
