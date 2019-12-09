/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.api.item.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.item.inventory.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.entity.living.human.HumanEntity;
import org.spongepowered.common.item.util.ItemStackUtil;
import org.spongepowered.common.mixin.api.mcp.entity.EntityLivingBaseMixin_API;

import javax.annotation.Nullable;

// All living implementors of ArmorEquipable
@Mixin({ArmorStandEntity.class, GiantEntity.class, ServerPlayerEntity.class, AbstractSkeletonEntity.class, ZombieEntity.class, HumanEntity.class})
public abstract class ArmorEquippableMixin_API extends EntityLivingBaseMixin_API implements ArmorEquipable {

    @Override
    public ItemStack getItemInHand(HandType handType) {
        checkNotNull(handType, "HandType cannot be null!");
        final net.minecraft.item.ItemStack nmsItem = this.getHeldItem((Hand) (Object) handType);
        return ItemStackUtil.fromNative(nmsItem);
    }

    @Override
    public void setItemInHand(HandType handType, @Nullable ItemStack itemInHand) {
        checkNotNull(handType, "HandType cannot be null!");
        this.setHeldItem((Hand) (Object) handType, ItemStackUtil.toNative(itemInHand).copy());
    }
}