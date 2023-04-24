package net.offhandswitcher.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.offhandswitcher.network.SyncOffHandStatePacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        var offhand = ((HasOffHandSwitchState) player.getInventory());
        SyncOffHandStatePacket.sendS2C(player, offhand.getOffSideSlot(), offhand.getOffHandSwitchState());
    }

}
