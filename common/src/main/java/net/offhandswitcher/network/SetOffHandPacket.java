package net.offhandswitcher.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.offhandswitcher.OffHandSwitcherMod;

public class SetOffHandPacket {
    public static final Identifier ID = new Identifier(OffHandSwitcherMod.MOD_ID, "set_offhand");

    public static void sendC2S(int from, int to) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(((from << 4) | to) & 0xFF);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void receiveC2S(PacketByteBuf buf, NetworkManager.PacketContext context) {
        var player = context.getPlayer();

        int value = buf.readByte();
        int from = value >> 4;
        int to = value & 0xF;
        context.queue(() -> {
            var inventory = player.getInventory();
            //まずoffHandにおいてあるアイテムをfromに入れる
            //fromが埋まってるならなにもせず、後の工程でtoとoffhandを交換する
            if (!inventory.offHand.get(0).isEmpty()) {
                if (inventory.main.get(from).isEmpty()) {
                    inventory.main.set(from, inventory.offHand.get(0));
                    inventory.offHand.set(0, ItemStack.EMPTY);
                } else {
                    //なんもせん
                }
            }
            //toとoffhandを交換する
            var toItem = inventory.main.get(to);
            var offItem = inventory.offHand.get(0);
            inventory.offHand.set(0, toItem);
            inventory.main.set(to, offItem);
        });
    }

}
