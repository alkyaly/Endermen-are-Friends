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

        if (stack.getItem() instanceof BlockItem && !world.isClient) {
            BlockItem item = (BlockItem) stack.getItem();

            if (item.getBlock() instanceof FlowerBlock) {
                if (state != null) {
                    dropItem(state.getBlock());
                }

                setCarriedBlock(item.getBlock().getDefaultState());
                stack.decrement(1);

                for (int i = 0; i < 4; ++i) {
                    world.addParticle(ParticleTypes.HEART, getParticleX(0.5D), getRandomBodyY() + 1.25D, getParticleZ(0.5), (random.nextDouble() - 0.5) * 2, -random.nextDouble(), (random.nextDouble() - 0.5) * 2);
                }
                stopAnger();

                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }
}
