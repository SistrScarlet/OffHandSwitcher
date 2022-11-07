package net.offhandswitcher.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.offhandswitcher.OffHandSwitcherMod;
import net.offhandswitcher.network.fabric.SetOffHandPacketImpl;
import net.offhandswitcher.network.fabric.SyncOffHandStatePacketImpl;
import net.offhandswitcher.util.HasOffHandSwitchState;

public class OffHandSwitcherModFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        OffHandSwitcherMod.init();
        ServerPlayNetworking.registerGlobalReceiver(SetOffHandPacketImpl.ID,
                (MinecraftServer server, ServerPlayerEntity player,
                 ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
                    int value = buf.readByte();
                    int from = value >> 4;
                    int to = value & 0xF;
                    server.execute(() -> {
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
                });
        ServerPlayNetworking.registerGlobalReceiver(SyncOffHandStatePacketImpl.ID,
                (MinecraftServer server, ServerPlayerEntity player,
                 ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
                    int value = buf.readByte();
                    int offhandSlot = value >> 1;
                    boolean offHandSwitch = (value & 1) == 1;
                    server.execute(() -> {
                        var inventory = player.getInventory();
                        ((HasOffHandSwitchState) inventory).setOffSideSlot(offhandSlot);
                        ((HasOffHandSwitchState) inventory).setOffHandSwitchState(offHandSwitch);
                    });
                });
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncOffHandStatePacketImpl.ID,
                (MinecraftClient client, ClientPlayNetworkHandler handler,
                 PacketByteBuf buf, PacketSender responseSender) -> {
                    int value = buf.readByte();
                    int offhandSlot = value >> 1;
                    boolean offHandSwitch = (value & 1) == 1;
                    client.execute(() -> {
                        if (client.player == null) {
                            return;
                        }
                        var inventory = client.player.getInventory();
                        ((HasOffHandSwitchState) inventory).setOffSideSlot(offhandSlot);
                        ((HasOffHandSwitchState) inventory).setOffHandSwitchState(offHandSwitch);
                    });
                });
    }
}
