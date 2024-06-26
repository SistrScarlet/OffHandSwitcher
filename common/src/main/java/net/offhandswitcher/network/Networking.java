package net.offhandswitcher.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Networking {

    public static void commonInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SetOffHandPacket.ID, SetOffHandPacket::receiveC2S);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SyncOffHandStatePacket.ID, SyncOffHandStatePacket::receiveC2S);
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SyncOffHandStatePacket.ID, SyncOffHandStatePacket::receiveS2C);
    }

}
