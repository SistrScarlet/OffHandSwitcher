package net.offhandswitcher.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.offhandswitcher.util.HasOffHandSwitchState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Shadow
    @Final
    private PlayerInventory inventory;

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putByte("SelectOffSlot", (byte) ((HasOffHandSwitchState) this.inventory).getOffSideSlot());
        nbt.putBoolean("SelectOffSwitch", ((HasOffHandSwitchState) this.inventory).getOffHandSwitchState());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        ((HasOffHandSwitchState) this.inventory).setOffSideSlot(nbt.getByte("SelectOffSlot"));
        ((HasOffHandSwitchState) this.inventory).setOffHandSwitchState(nbt.getBoolean("SelectOffSwitch"));
        //実行タイミングが遅いのでここでやる
        this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
    }

}
