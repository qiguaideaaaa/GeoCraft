package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;

import javax.annotation.Nullable;

public abstract class GasState implements IAtmosphereState, IFluidHandler {
    protected final Fluid gas;
    protected int amount = 0;
    public GasState(Fluid fluid, int amount){
        this.gas = fluid;
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

    public Fluid getGas() {
        return gas;
    }

    @Override
    public void onUpdate(Atmosphere atmosphere, Chunk chunk) {}

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
        if(resource.getFluid() != gas) return 0;
        if(this.amount + resource.amount <0) return 0;
        if(doFill) addAmount(resource.amount);
        return resource.amount;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(resource.getFluid() != gas) return null;
        return drain(resource.amount,doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        int drainedInFact = (this.amount- maxDrain <0)?this.amount: maxDrain;
        if(doDrain) addAmount(-drainedInFact);
        return new FluidStack(gas,drainedInFact);
    }

    public class GasStateFluidTankProperties implements IFluidTankProperties{

        @Nullable
        @Override
        public FluidStack getContents() {
            return null;
        }

        @Override
        public int getCapacity() {
            return 0;
        }

        @Override
        public boolean canFill() {
            return true;
        }

        @Override
        public boolean canDrain() {
            return true;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return fluidStack.getFluid() == gas;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return fluidStack.getFluid() == gas;
        }
    }
}
