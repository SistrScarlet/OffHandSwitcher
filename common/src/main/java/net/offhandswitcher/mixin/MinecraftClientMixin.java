package net.offhandswitcher.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.offhandswitcher.network.SetOffHandPacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    private int from;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    public GameOptions options;

    @Inject(method = "handleInputEvents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onHandleInputEventsBefore(CallbackInfo ci, int i) {
        var switchState = ((HasOffHandSwitchState) this.player.getInventory());
        if (switchState.getOffHandSwitchState()) {
            this.from = this.player.getInventory().selectedSlot;
        }
    }

    @Inject(method = "handleInputEvents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onHandleInputEventsAfter(CallbackInfo ci, int i) {
        var switchState = ((HasOffHandSwitchState) this.player.getInventory());
        if (switchState.getOffHandSwitchState()) {
            int slot = Math.min(i, 3);
            this.player.getInventory().selectedSlot = slot;
            if (from < 4) {
                SetOffHandPacket.sendC2S(from, slot);
            }
        } else {
            this.player.getInventory().selectedSlot = Math.min(i + 4, 8);
        }
    }

}
