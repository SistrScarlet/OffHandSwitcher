package net.offhandswitcher.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerNetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof PlayerActionC2SPacket actionPacket) {
            if (actionPacket.getAction() != PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
                return;
            }
            var player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }
            if (!player.isSpectator()) {
                var inventory = player.getInventory();
                var switchState = ((HasOffHandSwitchState) inventory);
                switchState.setOffHandSwitchState(!switchState.getOffHandSwitchState());
                var tmp = inventory.selectedSlot;
                inventory.selectedSlot = switchState.getOffSideSlot();
                switchState.setOffSideSlot(tmp);
                ci.cancel();
            }
        }
    }

}
