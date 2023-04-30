package net.offhandswitcher.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
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
public abstract class PlayerInventoryMixin implements HasOffHandSwitchState {
    private boolean offHandSwitchState;
    private int offSideSlot;
    @Shadow
    public int selectedSlot;
    @Shadow
    @Final
    public DefaultedList<ItemStack> main;
    private final Item[] dropItems = new Item[9];

    @Shadow
    public abstract ItemStack removeStack(int slot, int amount);

    @Shadow
    public abstract int getOccupiedSlotWithRoomForStack(ItemStack stack);

    @Shadow
    protected abstract int addStack(int slot, ItemStack stack);

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow
    public abstract void setStack(int slot, ItemStack stack);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(PlayerEntity player, CallbackInfo ci) {
        selectedSlot = 4;
    }

    @Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
    private void onGetMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        if (offHandSwitchState) {
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

    @Inject(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void onAddStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int index = this.getOccupiedSlotWithRoomForStack(stack);
        if (index == -1) {
            for (int i = 0; i < 9; i++) {
                if (!this.getStack(i).isEmpty()) continue;
                var item = dropItems[i];
                if (item != null && item == stack.getItem()) {
                    dropItems[i] = null;
                    if (i == selectedSlot && i < 4) {
                        i = 40;
                    }
                    cir.setReturnValue(this.addStack(i, stack));
                    return;
                }
            }
        }

        if (index == -1) {
            for (int k = 9; k < this.main.size(); ++k) {
                if (!this.main.get(k).isEmpty()) continue;
                index = k;
                break;
            }
        }
        if (index == -1) {
            index = stack.getCount();
        }
        cir.setReturnValue(this.addStack(index, stack));

    }

    //ホットバーのオフハンドにアイテムが入った時、オフハンドに送る
    /*@Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (offHandSwitchState) {
            if (this.getStack(40).isEmpty() && slot == selectedSlot) {
                this.setStack(40, stack);
                ci.cancel();
            }
        } else {
            if (this.getStack(40).isEmpty() && slot == offSideSlot) {
                this.setStack(40, stack);
                ci.cancel();
            }
        }
    }*/

    @Inject(method = "addPickBlock", at = @At("HEAD"), cancellable = true)
    private void onAddPickBlock(ItemStack stack, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At("HEAD"))
    private void onGetOcc(ItemStack stack, CallbackInfoReturnable<Integer> cir) {

    }

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void onUpdateItems(CallbackInfo ci) {

    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"))
    private void onGetBBS(BlockState block, CallbackInfoReturnable<Float> cir) {

    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        int index;
        if (offHandSwitchState) {
            index = 40;
        } else {
            index = selectedSlot;
        }
        ItemStack itemStack = this.getStack(index);
        if (itemStack.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
        dropItems[selectedSlot] = itemStack.getItem();
        cir.setReturnValue(this.removeStack(index, entireStack ? itemStack.getCount() : 1));
    }

    @Override
    public void setOffHandSwitchState(boolean switchState) {
        this.offHandSwitchState = switchState;
    }

    @Override
    public boolean getOffHandSwitchState() {
        return this.offHandSwitchState;
    }

    @Override
    public void setOffSideSlot(int offSideSlot) {
        this.offSideSlot = offSideSlot;
    }

    @Override
    public int getOffSideSlot() {
        return this.offSideSlot;
    }
}
