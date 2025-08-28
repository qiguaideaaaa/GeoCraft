package top.qiguaiaaaa.geocraft.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;

import javax.annotation.Nullable;

public abstract class FluidState implements GeographyState, IFluidHandler {
    protected final Fluid fluid;
    protected int amount;
    public FluidState(Fluid fluid, int amount){
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
    public void initialise(Layer layer) {
        this.amount = 0;
    }

    @Override
    public boolean isInitialised() {
        return amount>=0;
    }

    @Override
    public abstract FluidProperty getProperty() ;

    /**
     * 获取气体对应的Forge流体
     * @return Forge流体
     */
    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public NBTBase serializeNBT() {
        return new NBTTagInt(amount);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            this.amount = ((NBTPrimitive) nbt).getInt();
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new GasStateFluidTankProperties[]{new GasStateFluidTankProperties()};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(resource.getFluid() != fluid) return 0;
        if(this.amount + resource.amount <0) return 0;
        if(doFill) addAmount(resource.amount);
        return resource.amount;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
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

    @Override
    public String toString() {
        return amount+"";
    }

    public class GasStateFluidTankProperties extends FluidTankProperties {

        public GasStateFluidTankProperties() {
            super(null, 99999999,true,true);
        }

        @Override
        public FluidStack getContents() {
            return new FluidStack(fluid,getAmount());
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return fluidStack.getFluid() == fluid;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return fluidStack.getFluid() == fluid;
        }
    }
}
