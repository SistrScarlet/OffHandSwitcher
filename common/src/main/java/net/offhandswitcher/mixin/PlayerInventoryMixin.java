package net.offhandswitcher.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.offhandswitcher.network.SetOffHandPacket;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin implements HasOffHandSwitchState {
    private boolean offHandSwitchState;
    private int offSideSlot;
    @Shadow
    public int selectedSlot;
    @Shadow
    @Final
    public DefaultedList<ItemStack> main;

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
    private void onGetMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        if (this.player.world.isClient && offHandSwitchState) {
            cir.setReturnValue(this.main.get(offSideSlot));
        }
    }

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void onScrollInHotbar(double scrollAmount, CallbackInfo ci) {
        int i = (int) Math.signum(scrollAmount);
        if (offHandSwitchState) {
            int from = this.selectedSlot;

            for (this.selectedSlot -= i; this.selectedSlot < 0; this.selectedSlot += 4) {
            }

            while (4 <= this.selectedSlot) {
                this.selectedSlot -= 4;
            }
            int to = this.selectedSlot;
            if (from < 4) {
                SetOffHandPacket.sendC2S(from, to);
            }
        } else {
            for (this.selectedSlot -= i; this.selectedSlot < 4; this.selectedSlot += 5) {
            }

            while (9 <= this.selectedSlot) {
                this.selectedSlot -= 5;
            }
        }
        ci.cancel();
    }

    @Inject(method = "addPickBlock", at = @At("HEAD"), cancellable = true)
    private void onAddPickBlock(ItemStack stack, CallbackInfo ci) {
        ci.cancel();
    }

    @Override
    public void setOffHandSwitchState(boolean switchState) {
        if (switchState != this.offHandSwitchState) {
            int tmp = this.selectedSlot;
            this.selectedSlot = this.offSideSlot;
            this.offSideSlot = tmp;
            this.offHandSwitchState = switchState;
        }
    }

    @Override
    public boolean getOffHandSwitchState() {
        return this.offHandSwitchState;
    }

    @Override
    public int getOffSideSlot() {
        return this.offSideSlot;
    }
}
