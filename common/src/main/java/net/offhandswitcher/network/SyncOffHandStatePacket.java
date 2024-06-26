package net.offhandswitcher.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.offhandswitcher.OffHandSwitcherMod;
import net.offhandswitcher.util.HasOffHandSwitchState;

public class SyncOffHandStatePacket {
    public static final Identifier ID = new Identifier(OffHandSwitcherMod.MOD_ID);

    public static void sendC2S(int offhandSlot, boolean switchOffhand) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte((offhandSlot << 1) | (switchOffhand ? 1 : 0));
        NetworkManager.sendToServer(ID, buf);
    }

    public static void sendS2C(ServerPlayerEntity player, int offhandSlot, boolean switchOffhand) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte((offhandSlot << 1) | (switchOffhand ? 1 : 0));
        NetworkManager.sendToPlayer(player, ID, buf);
    }

    public static void receiveC2S(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int value = buf.readByte();
        int offhandSlot = value >> 1;
        boolean offHandSwitch = (value & 1) == 1;
        var player = context.getPlayer();
        context.queue(() -> {
            var inventory = player.getInventory();
            ((HasOffHandSwitchState) inventory).setOffSideSlot(offhandSlot);
            ((HasOffHandSwitchState) inventory).setOffHandSwitchState(offHandSwitch);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void receiveS2C(PacketByteBuf buf, NetworkManager.PacketContext context) {
        int value = buf.readByte();
        int offhandSlot = value >> 1;
        boolean offHandSwitch = (value & 1) == 1;
        var player = context.getPlayer();
        context.queue(() -> {
            if (player == null) {
                return;
            }
            var inventory = player.getInventory();
            ((HasOffHandSwitchState) inventory).setOffSideSlot(offhandSlot);
            ((HasOffHandSwitchState) inventory).setOffHandSwitchState(offHandSwitch);
        });
    }
}
