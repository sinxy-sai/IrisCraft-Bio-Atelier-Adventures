// PaintTableUpdatePacket.java
package com.bnuz.mod.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import com.bnuz.mod.BnuzMod;
import com.bnuz.mod.block.PaintTable_Entity;

public class PaintTableUpdatePacket {
    public static final Identifier ID = new Identifier(BnuzMod.MOD_ID, "paint_table_update");

    private final BlockPos pos;
    private final int[] pixels;

    public PaintTableUpdatePacket(BlockPos pos, int[] pixels) {
        this.pos = pos;
        this.pixels = pixels;
    }

    public static void encode(PaintTableUpdatePacket packet, PacketByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeIntArray(packet.pixels);
    }

    public static PaintTableUpdatePacket decode(PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int[] pixels = buf.readIntArray();
        return new PaintTableUpdatePacket(pos, pixels);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player,
                              ServerPlayNetworkHandler handler, PacketByteBuf buf,
                              PacketSender responseSender) {
        PaintTableUpdatePacket packet = decode(buf);

        server.execute(() -> {
            if (player.getWorld().getBlockEntity(packet.pos) instanceof PaintTable_Entity) {
                PaintTable_Entity blockEntity = (PaintTable_Entity) player.getWorld().getBlockEntity(packet.pos);
                blockEntity.setPixelData(packet.pixels);
            }
        });
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, PaintTableUpdatePacket::handle);
    }
}