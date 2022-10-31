package net.offhandswitcher.network.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.offhandswitcher.OffHandSwitcherMod;

public class SetOffHandPacketImpl {
    public static final Identifier ID = new Identifier(OffHandSwitcherMod.MOD_ID, "set_offhand");

    public static void sendC2S(int from, int to) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(((from << 4) | to) & 0xFF);
        ClientPlayNetworking.send(ID, buf);
    }
}
