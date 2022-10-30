package net.offhandswitcher.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow
    public int selectedSlot;

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void onScrollInHotbar(double scrollAmount, CallbackInfo ci) {
        int i = (int) Math.signum(scrollAmount);

        for (this.selectedSlot -= i; this.selectedSlot < 4; this.selectedSlot += 5) {
        }

        while (9 <= this.selectedSlot) {
            this.selectedSlot -= 5;
        }
        ci.cancel();
    }

    @Inject(method = "addPickBlock", at = @At("HEAD"), cancellable = true)
    private void onAddPickBlock(ItemStack stack, CallbackInfo ci) {
        ci.cancel();
    }

}
