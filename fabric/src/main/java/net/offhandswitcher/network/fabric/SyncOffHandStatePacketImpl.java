package net.offhandswitcher.network.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.offhandswitcher.OffHandSwitcherMod;

public class SyncOffHandStatePacketImpl {
    public static final Identifier ID = new Identifier(OffHandSwitcherMod.MOD_ID);

    public static void sendC2S(int offhandSlot, boolean switchOffhand) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte((offhandSlot << 1) | (switchOffhand ? 1 : 0));
        ClientPlayNetworking.send(ID, buf);
    }
}
