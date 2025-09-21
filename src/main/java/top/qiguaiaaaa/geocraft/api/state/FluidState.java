/*
 * Copyright 2025 QiguaiAAAA
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
 * 版权所有 2025 QiguaiAAAA
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

package top.qiguaiaaaa.geocraft.api.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 流体状态
 * @author QiguaiAAAA
 */
public abstract class FluidState implements GeographyState, IFluidHandler {
    protected final Fluid fluid;
    protected int amount;
    public FluidState(@Nonnull Fluid fluid, int amount){
        this.fluid = fluid;
        this.amount = amount;
    }

    public void setAmount(int gasAmount) {
        if(gasAmount<0) gasAmount = 0;
        this.amount = gasAmount;
    }

    public boolean addAmount(int amount){
        if(this.amount + amount <0) return false;
        this.amount += amount;
        return true;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void initialise(@Nonnull Layer layer) {
        this.amount = 0;
    }

    @Override
    public boolean isInitialised() {
        return amount>=0;
    }

    @Nonnull
    @Override
    public abstract FluidProperty getProperty() ;

    /**
     * 获取气体对应的Forge流体
     * @return Forge流体
     */
    @Nonnull
    public Fluid getFluid() {
        return fluid;
    }
    @Nonnull
    @Override
    public NBTBase serializeNBT() {
        return new NBTTagInt(amount);
    }

    @Override
    public void deserializeNBT(@Nonnull NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            this.amount = ((NBTPrimitive) nbt).getInt();
        }
    }
    @Nonnull
    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new GasStateFluidTankProperties[]{new GasStateFluidTankProperties()};
    }

    @Override
    public int fill(@Nonnull FluidStack resource, boolean doFill) {
        if(resource.getFluid() != fluid) return 0;
        if(this.amount + resource.amount <0) return 0;
        if(doFill) addAmount(resource.amount);
        return resource.amount;
    }

    @Nullable
    @Override
    public FluidStack drain(@Nonnull FluidStack resource, boolean doDrain) {
        if(resource.getFluid() != fluid) return null;
        return drain(resource.amount,doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        int drainedInFact = (this.amount- maxDrain <0)?this.amount: maxDrain;
        if(doDrain) addAmount(-drainedInFact);
        return new FluidStack(fluid,drainedInFact);
    }
    @Nonnull
    @Override
    public String toString() {
        return amount+"";
    }

    public class GasStateFluidTankProperties extends FluidTankProperties {

        public GasStateFluidTankProperties() {
            super(null, 99999999,true,true);
        }
        @Nonnull
        @Override
        public FluidStack getContents() {
            return new FluidStack(fluid,getAmount());
        }

        @Override
        public boolean canFillFluidType(@Nonnull FluidStack fluidStack) {
            return fluidStack.getFluid() == fluid;
        }

        @Override
        public boolean canDrainFluidType(@Nonnull FluidStack fluidStack) {
            return fluidStack.getFluid() == fluid;
        }
    }
}
