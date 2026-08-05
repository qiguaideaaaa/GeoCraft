/*
 * Copyright 2026 QGMoe
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2026 QGMoe
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package moe.qingu.orbtellus.api.fluidphysics;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.qingu.orbtellus.api.event.EventFactory;
import net.minecraft.block.BlockLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import moe.qingu.orbtellus.api.util.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * 流体物理系统
 * @since 0.3.0-alpha.2
 * @author QGMoe
 */
public final class FluidPhysicsSystem implements INBTSerializable<NBTTagCompound> {
    private static final HashSet<String> FLUIDS_NOT_TO_BE_PHYSICAL = new HashSet<>();
    private static final HashSet<String> FLUIDS_BUCKET_TO_BE_VANILLA = new HashSet<>();
    private static final Int2ObjectOpenHashMap<FluidPhysicsSystem> DIM2SYSTEMS = new Int2ObjectOpenHashMap<>();

    public final int dimension;
    /**
     * 相对重力，1 表示人类默认的重力，无单位，不能为 NaN 或 Infinite，暂时不能为负数
     */
    private double gravity = 1d;
    /**
     * 相对轻性，1 表示重力大小为 1 的情况下的轻性，无单位，为相对重力的倒数，可以为正无穷但不能为 NaN，暂时不能为负数
     */
    private double levity = 1d;

    private FluidPhysicsSystem(final int dimension){
        this.dimension = dimension;
    }

    public void setGravity(final double gravity) {
        if( !(gravity>=0d) || Double.isInfinite(gravity) ) throw new IllegalArgumentException();
        if(Math.abs(gravity)>=0.002d){
            this.gravity = gravity;
            this.levity = 1d/gravity;
        }else {
            this.gravity = 0d;
            this.levity = Double.POSITIVE_INFINITY;
        }
    }

    public boolean hasGravity(){
        return this.gravity > 0d;
    }

    /**
     * 获取重力相对于主世界的大小。
     * 例如，返回 100 表示该维度的重力是主世界的 100 倍，返回 0.1 表示该维度的重力是主世界的 0.1 倍。
     * 值的大小（目前）不应该是负的，也不会是 {@link Double#NaN} 或 {@link Double#isInfinite()}。
     * @since 0.3.0-alpha.2
     * @return 一个双精度浮点数，表示重力大小。当值为 0 时，表示无重力。
     */
    public double getGravity() {
        return gravity;
    }

    public double getLevity() {
        return levity;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("gravity",gravity);
        return compound;
    }

    @Override
    public void deserializeNBT(final @Nonnull NBTTagCompound nbt) {
        this.setGravity(nbt.getDouble("gravity"));
    }

    /* ==========================
             Static Area
       ========================== */

    /**
     * 设置指定流体是否需要被物理化
     * @since 0.3.0-alpha.2
     * @param fluidName 流体名
     * @param physical 是否需要物理化
     */
    public static void setFluidToBePhysical(final @Nonnull String fluidName,final boolean physical){
        if(physical) FLUIDS_NOT_TO_BE_PHYSICAL.remove(fluidName);
        else FLUIDS_NOT_TO_BE_PHYSICAL.add(fluidName);
    }

    public static void setFluidToUseVanillaBucketMode(String fluidName,boolean vanilla){
        if(vanilla) FLUIDS_BUCKET_TO_BE_VANILLA.add(fluidName);
        else FLUIDS_BUCKET_TO_BE_VANILLA.remove(fluidName);
    }

    /**
     * 指定流体是否需要物理化
     * @since 0.1
     * @param fluid 流体
     * @return 若需要,则返回true
     */
    public static boolean isFluidToBePhysical(Fluid fluid){
        if(fluid == null) return false;
        return !FLUIDS_NOT_TO_BE_PHYSICAL.contains(fluid.getName());
    }

    /**
     * @see #isFluidToBePhysical(Fluid)
     * @since 0.1
     */
    public static boolean isFluidToBePhysical(BlockLiquid fluid){
        return isFluidToBePhysical(FluidUtil.getFluid(fluid));
    }

    /**
     * 指定流体是否需要使用原版的桶行为
     * @since 0.1
     * @param fluid 流体
     * @return 若需要，则返回true
     */
    public static boolean isFluidToUseVanillaBucketMode(Fluid fluid){
        if(fluid == null) return true;
        return FLUIDS_BUCKET_TO_BE_VANILLA.contains(fluid.getName());
    }

    public static FluidPhysicsSystem getSystem(final @Nonnull World world){
        return DIM2SYSTEMS.get(world.provider.getDimension());
    }

    public static FluidPhysicsSystem getSystem(final int dimensionID){
        return DIM2SYSTEMS.get(dimensionID);
    }

    @SideOnly(Side.CLIENT)
    public static void syncFromServer(final @Nonnull NBTTagCompound compound){
        DIM2SYSTEMS.clear();
        for(final String k:compound.getKeySet()){
            if(k.length() != 2) continue;
            final int dim = (k.charAt(0) << 16) | k.charAt(1);
            final FluidPhysicsSystem system = new FluidPhysicsSystem(dim);
            system.deserializeNBT(compound.getCompoundTag(k));
            DIM2SYSTEMS.put(dim,system);
        }
    }

    @Nonnull
    public static NBTTagCompound serializeForClient(){
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("mode", (byte) FluidPhysicsMode.getCurrentMode().ordinal());
        for(final Int2ObjectMap.Entry<FluidPhysicsSystem> entry: DIM2SYSTEMS.int2ObjectEntrySet())
            compound.setTag(new String(new char[]{(char) (entry.getIntKey()>>>16), (char) (entry.getIntKey()&0xFFFF)}),entry.getValue().serializeNBT());
        return compound;
    }

    public static void onServerStop(){
        DIM2SYSTEMS.clear();
    }

    @Nonnull
    public static FluidPhysicsSystem createFluidPhysicsSystem(final @Nonnull World world){
        final int dim = world.provider.getDimension();
        final FluidPhysicsSystem system = new FluidPhysicsSystem(dim);
        EventFactory.onFluidPhysicsSystemLoad(world,system);
        DIM2SYSTEMS.put(dim,system);
        return system;
    }
}
