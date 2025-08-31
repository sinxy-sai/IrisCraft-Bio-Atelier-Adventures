package com.bnuz.mod.item;

import com.bnuz.mod.entity.CustomPaintingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CustomPaintingItem extends Item {
    public CustomPaintingItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        World world = context.getWorld();

        // 计算画作的位置
        BlockPos placementPos = blockPos.offset(direction);

        // 检查是否可以放置
        if (player != null && !canPlaceOn(player, direction, stack, placementPos)) {
            return ActionResult.FAIL;
        }

        // 创建自定义画作实体
        CustomPaintingEntity painting = new CustomPaintingEntity(
                world, placementPos, direction
        );

        // 设置像素数据
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("pixels")) {
            painting.setPixelData(nbt.getIntArray("pixels"));
            painting.setImagePath(nbt.getString("imagePath"));
        }

        // 尝试放置
        if (painting.canStayAttached()) {
            if (!world.isClient) {
                painting.onPlace();
                world.emitGameEvent(player, GameEvent.ENTITY_PLACE, blockPos);
                world.spawnEntity(painting);

                // 消耗物品
                if (player != null && !player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }

    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }
}