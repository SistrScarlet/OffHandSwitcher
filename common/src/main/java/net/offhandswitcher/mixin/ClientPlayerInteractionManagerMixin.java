package net.offhandswitcher.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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
    @Shadow
    private int lastSelectedSlot;
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "syncSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void onSyncSelectedSlot(CallbackInfo ci) {
        int slot;
        var inventory = this.client.player.getInventory();
        var offhandState = ((HasOffHandSwitchState) inventory);
        if (offhandState.getOffHandSwitchState()) {
            slot = offhandState.getOffSideSlot();
        } else {
            slot = inventory.selectedSlot;
        }
        if (slot != this.lastSelectedSlot) {
            this.lastSelectedSlot = slot;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
        }
        ci.cancel();
    }

}
