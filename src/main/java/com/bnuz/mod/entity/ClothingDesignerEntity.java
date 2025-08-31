package com.bnuz.mod.entity;

import com.bnuz.mod.BnuzMod;
import com.bnuz.mod.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class ClothingDesignerEntity extends MerchantEntity implements NamedScreenHandlerFactory {

    private boolean recipesFilled = false;   // 只做一次

    public ClothingDesignerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
        this.initGoals();
    }

    @Override
    protected void fillRecipes() {
        if (recipesFilled || this.getWorld().isClient) return;
        this.recipesFilled = true;
        this.getOffers().clear();
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.OAK_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.SPRUCE_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.BIRCH_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.JUNGLE_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.ACACIA_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
        this.getOffers().add(new TradeOffer(
                new ItemStack(Items.DARK_OAK_LEAVES, 4),
                new ItemStack(ModItems.SILK, 1),
                12, 3, 0.075f
        ));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient) {
            this.fillRecipes();
            this.setCustomer(player);
            this.sendOffers(player, Text.empty(), 0);
        }
        return ActionResult.success(this.getWorld().isClient);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MerchantScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected void initGoals() {
        // 基础移动
        this.goalSelector.add(0, new WanderAroundGoal(this, 0.6D));
        // 看向最近的玩家
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        // 随机看四周（防止呆滞）
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void setOffersFromServer(@Nullable TradeOfferList offers) {
        this.offers = offers;
    }

    @Override
    protected void afterUsing(TradeOffer offer) {

    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("entity.bnuz-mod.clothing_designer");
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
