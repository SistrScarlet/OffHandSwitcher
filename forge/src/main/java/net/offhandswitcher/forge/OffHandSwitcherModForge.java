package net.offhandswitcher.forge;

import net.offhandswitcher.OffHandSwitcherMod;
import net.minecraftforge.fml.common.Mod;

@Mod(OffHandSwitcherMod.MOD_ID)
public class OffHandSwitcherModForge {
    public OffHandSwitcherModForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(OffHandSwitcherMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OffHandSwitcherMod.init();
    }
}
