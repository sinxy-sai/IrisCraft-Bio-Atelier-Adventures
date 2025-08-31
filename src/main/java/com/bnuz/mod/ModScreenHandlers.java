package com.bnuz.mod;


import com.bnuz.mod.block.PaintTable;
import com.bnuz.mod.block.PaintTable_Entity;
import com.bnuz.mod.block.PaintTable_ScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModScreenHandlers {
    public static final ScreenHandlerType<PaintTable_ScreenHandler> PAINT_TABLE_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    new Identifier(BnuzMod.MOD_ID, "paint_table"),
                    (syncId, playerInventory, buf) -> new PaintTable_ScreenHandler(
                            syncId,
                            playerInventory,
                            (PaintTable_Entity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos())
                    )
            );
    public static void registerAll() {
    }
}
