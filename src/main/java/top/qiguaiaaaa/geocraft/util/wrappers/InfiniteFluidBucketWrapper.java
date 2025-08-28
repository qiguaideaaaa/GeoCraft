package top.qiguaiaaaa.geocraft.util.wrappers;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class InfiniteFluidBucketWrapper implements IFluidHandler {
    public static final InfiniteFluidBucketWrapper INFINITE_WATER_BUCKET_WRAPPER = new InfiniteFluidBucketWrapper(FluidRegistry.WATER);
    public static final InfiniteFluidBucketWrapper INFINITE_LAVA_BUCKET_WRAPPER = new InfiniteFluidBucketWrapper(FluidRegistry.LAVA);
    protected final IFluidTankProperties tankProperties;
    protected final FluidStack fluidStack;
    protected final Fluid fluid;
    public InfiniteFluidBucketWrapper(Fluid fluid){
        this.fluid = fluid;
        fluidStack = new FluidStack(fluid,Integer.MAX_VALUE);
        tankProperties = new FluidTankProperties(fluidStack,Integer.MAX_VALUE,false,true);
    }
    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] {tankProperties};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(resource.getFluid() != fluid) return null;
        return new FluidStack(fluid,resource.amount);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return new FluidStack(fluid,maxDrain);
    }
}
