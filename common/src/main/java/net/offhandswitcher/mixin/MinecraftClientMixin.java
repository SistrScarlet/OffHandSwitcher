package net.offhandswitcher.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.offhandswitcher.network.SetOffHandPacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    private int from;
    private int fromOff;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "handleInputEvents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onHandleInputEventsBefore(CallbackInfo ci, int i) {
        this.from = this.player.getInventory().selectedSlot;
        var switchState = ((HasOffHandSwitchState) this.player.getInventory());
        this.from = this.player.getInventory().selectedSlot;
        this.fromOff = switchState.getOffSideSlot();
    }

    @Inject(method = "handleInputEvents",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onHandleInputEventsAfter(CallbackInfo ci, int i) {
        var switchState = ((HasOffHandSwitchState) this.player.getInventory());
        if (!switchState.getOffHandSwitchState()) {
            if (4 <= i) {
                this.player.getInventory().selectedSlot = i;
            } else {
                this.player.getInventory().selectedSlot = from;
                switchState.setOffSideSlot(i);
                if (fromOff != i) {
                    SetOffHandPacket.sendC2S(fromOff, i);
                }
            }
        } else {
            if (i < 4) {
                this.player.getInventory().selectedSlot = i;
                if (from != i) {
                    SetOffHandPacket.sendC2S(from, i);
                }
            } else {
                this.player.getInventory().selectedSlot = from;
                switchState.setOffSideSlot(i);
            }
        }
    }

}
