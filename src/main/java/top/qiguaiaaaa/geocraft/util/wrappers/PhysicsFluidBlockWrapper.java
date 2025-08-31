package top.qiguaiaaaa.geocraft.util.wrappers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import top.qiguaiaaaa.geocraft.api.util.math.PlaceChoice;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;

import java.util.Set;

public class PhysicsFluidBlockWrapper extends FluidBlockWrapper {
    public PhysicsFluidBlockWrapper(IFluidBlock fluidBlock, World world, BlockPos blockPos) {
        super(fluidBlock, world, blockPos);
    }
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int placedAmount = 0;
        if (resource == null) {
            return 0;
        }

        final Set<PlaceChoice> choices = FluidSearchUtil.findPlaceableLocations(world, blockPos, fluidBlock.getFluid(), 16);
        if (choices.isEmpty()) return 0;
        int amountLeft = resource.amount;
        for (PlaceChoice choice : choices) {
            int amount = fluidBlock.place(world, choice.pos, new FluidStack(resource, amountLeft), doFill);
            amountLeft -= amount;
            placedAmount += amount;
            if (amountLeft <= 0) break;
        }

        return placedAmount;
    }
}
