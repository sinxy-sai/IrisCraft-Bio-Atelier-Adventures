package com.bnuz.mod;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ModEventHandlers {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (world.isClient) return TypedActionResult.pass(stack);
            if (stack.getItem() != Items.GLASS_BOTTLE) return TypedActionResult.pass(stack);

            // 找到玩家指向的水源
            HitResult hit = player.raycast(5.0D, 0.0F, true);
            if (!(hit instanceof BlockHitResult bhr)) return TypedActionResult.pass(stack);
            BlockPos pos = bhr.getBlockPos();
            if (!world.getFluidState(pos).isIn(net.minecraft.registry.tag.FluidTags.WATER)) {
                return TypedActionResult.pass(stack);
            }

            // 50 % 概率
            boolean isEcoli = world.random.nextFloat() < 0.4F;

            // 消耗 1 个玻璃瓶
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            ItemStack result;
            if (isEcoli) {
                // 大肠杆菌物品
                result = new ItemStack(ModItems.E_COLI);
            } else {
                // 原版水瓶
                result = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            }

            // 把结果塞给玩家（优先主手，没有空位就掉地上）
            if (!player.getInventory().insertStack(result)) {
                player.dropItem(result, false);
            }

            // 播放装水音效
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL,
                    1.0F, 1.0F);

            return TypedActionResult.success(stack, world.isClient);
        });
    }
}
