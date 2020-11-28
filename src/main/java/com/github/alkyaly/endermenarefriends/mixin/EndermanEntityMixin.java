package com.github.alkyaly.endermenarefriends.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MobEntity implements Angerable {

    @Shadow public @Nullable abstract BlockState getCarriedBlock();


    @Shadow public abstract void setCarriedBlock(@Nullable BlockState state);

    protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        BlockState state = getCarriedBlock();
        if(!(stack.getItem() instanceof BlockItem)) return ActionResult.FAIL;

        if (((BlockItem)stack.getItem()).getBlock() instanceof FlowerBlock) {
            if(state != null) dropItem(state.getBlock());
            stack.useOnEntity(player, this, hand);
            setCarriedBlock(((BlockItem)stack.getItem()).getBlock().getDefaultState());
            stack.decrement(1);
            if (world.isClient) {
                for (int i = 0; i < 4; ++i) {
                    this.world.addParticle(ParticleTypes.HEART, this.getParticleX(0.5D), this.getRandomBodyY() + 1.25D, this.getParticleZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
            stopAnger();
            return ActionResult.SUCCESS;
        } else return super.interactMob(player, hand);
    }
}
