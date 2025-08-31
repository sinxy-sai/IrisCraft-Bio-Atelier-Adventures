package com.bnuz.mod.block;

import com.bnuz.mod.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import com.bnuz.mod.ModScreenHandlers;

public class PaintTable_ScreenHandler extends ScreenHandler{
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final PaintTable_Entity blockEntity;
    private final PlayerEntity player;

    public PaintTable_ScreenHandler(int syncId, PlayerInventory playerInventory,PaintTable_Entity blockEntity){
        this(syncId, playerInventory, blockEntity, playerInventory.player);
    }

    public PaintTable_ScreenHandler(int syncId, PlayerInventory playerInventory,PaintTable_Entity blockEntity, PlayerEntity player){
        super(ModScreenHandlers.PAINT_TABLE_SCREEN_HANDLER,syncId);
        this.inventory = blockEntity;
        this.propertyDelegate = blockEntity.propertyDelegate;
        this.blockEntity = blockEntity;
        this.player = player;
        this.addProperties(propertyDelegate);  // 注册属性到客户端以同步数据

        // 丝绸输入槽
        this.addSlot(new Slot(blockEntity, PaintTable_Entity.SILK_SLOT, 48, 17) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.SILK);
            }
        });

        // 大肠杆菌输入槽
        this.addSlot(new Slot(blockEntity, PaintTable_Entity.ECOLI_SLOT, 48, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.E_COLI);
            }
        });

        // 水凝胶输入槽
        this.addSlot(new Slot(blockEntity, PaintTable_Entity.HYDROGEL_SLOT, 48, 53) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.HYDROGEL);
            }
        });

        // 输出槽
        this.addSlot(new Slot(blockEntity, PaintTable_Entity.OUTPUT_SLOT, 108, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // 输出槽不可放入物品
            }
        });

        // 添加玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 快捷栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

    }

    public PaintTable_Entity getBlockEntity() {
        return blockEntity;
    }


    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // 玩家关闭界面时，从交互列表中移除
        this.blockEntity.removeInteractingPlayer(player.getUuid());
    }

    // 获取绘制进度（箭头动画）
    public int getPaintProgress() {
        int currentTime = propertyDelegate.get(0); // 当前进度
        int totalTime = propertyDelegate.get(1);   // 总时间
        return totalTime != 0 ? (currentTime * 24) / totalTime : 0; // 25像素宽度
    }

    // 快速移动物品逻辑（Shift+点击）
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // 从机器槽位移到玩家背包
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 从玩家背包移到机器槽位
            else {
                ItemStack stackCopy = originalStack.copy();

                // 尝试放入丝绸槽
                if (stackCopy.isOf(ModItems.SILK)) {
                    if (!this.insertItem(originalStack, PaintTable_Entity.SILK_SLOT, PaintTable_Entity.SILK_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 尝试放入大肠杆菌槽
                else if (stackCopy.isOf(ModItems.E_COLI)) {
                    if (!this.insertItem(originalStack, PaintTable_Entity.ECOLI_SLOT, PaintTable_Entity.ECOLI_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 尝试放入水凝胶槽
                else if (stackCopy.isOf(ModItems.HYDROGEL)) {
                    if (!this.insertItem(originalStack, PaintTable_Entity.HYDROGEL_SLOT, PaintTable_Entity.HYDROGEL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 其他情况移到主背包
                else if (invSlot < this.inventory.size() + 27) { // 主背包 -> 快捷栏
                    if (!this.insertItem(originalStack, this.inventory.size() + 27, this.inventory.size() + 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 快捷栏 -> 主背包
                else if (!this.insertItem(originalStack, this.inventory.size(), this.inventory.size() + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

}
