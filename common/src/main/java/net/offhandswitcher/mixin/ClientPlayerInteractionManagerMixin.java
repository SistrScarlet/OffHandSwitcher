package net.offhandswitcher.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.offhandswitcher.network.SyncOffHandStatePacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    private int lastSelectedOffSlot;
    private boolean lastOffSwitch;

    @Inject(method = "syncSelectedSlot", at = @At("HEAD"))
    private void onSyncSelectedSlot(CallbackInfo ci) {
        var inventory = this.client.player.getInventory();
        var offhandState = ((HasOffHandSwitchState) inventory);
        if (offhandState.getOffHandSwitchState() != lastOffSwitch
                || offhandState.getOffSideSlot() != lastSelectedOffSlot) {
            lastOffSwitch = offhandState.getOffHandSwitchState();
            lastSelectedOffSlot = offhandState.getOffSideSlot();
            SyncOffHandStatePacket.sendC2S(offhandState.getOffSideSlot(),
                    offhandState.getOffHandSwitchState());
        }
    }

}
