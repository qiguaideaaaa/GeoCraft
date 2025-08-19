package top.qiguaiaaaa.fluidgeography.util.wrappers;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.FlowChoice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static top.qiguaiaaaa.fluidgeography.util.wrappers.PhysicsBlockLiquidWrapper.SIDES;

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
        inner_fill:{
            BlockPos placePos = findPlaceableLocation();
            if(placePos == null){
                break inner_fill;
            }
            final int amount = resource.amount;
            placedAmount = fluidBlock.place(world, placePos, resource, doFill);
            if(placedAmount>= amount) break inner_fill;
            List<FlowChoice> choices = new ArrayList<>(); //这里choice的quanta单位是mB
            int amountLeft = amount - placedAmount;
            for(EnumFacing facing:SIDES){
                BlockPos facingPos = placePos.offset(facing);
                if(!world.isBlockLoaded(facingPos)) continue;
                if(!isPlaceable(facingPos)) continue;
                int facingAmount = FluidUtil.getFluidAmount(world,facingPos,world.getBlockState(facingPos));
                choices.add(new FlowChoice(facingAmount,facing));
            }
            if(!choices.isEmpty()){
                choices.sort(Comparator.comparingInt(FlowChoice::getQuanta));
                for(FlowChoice choice:choices){
                    placedAmount += fluidBlock.place(world,placePos.offset(choice.direction),new FluidStack(resource,amountLeft),doFill);
                    amountLeft = amount - placedAmount;
                    if(amountLeft<=0) break;
                }
            }
            if(amountLeft<=0) break inner_fill;
            BlockPos upPos = placePos.up();
            if(!isPlaceable(upPos)) break inner_fill;
            placedAmount += fluidBlock.place(world,upPos,new FluidStack(resource,amountLeft),doFill);
        }
        return placedAmount;
    }
    protected BlockPos findPlaceableLocation(){
        BlockPos.MutableBlockPos nowPos = new BlockPos.MutableBlockPos(blockPos);
        while(!isPlaceable(nowPos)){
            nowPos.setPos(nowPos.getX(),nowPos.getY()+1, nowPos.getZ());
            if(nowPos.getY()>255) return null;
        }
        return nowPos;
    }
    protected boolean isPlaceable(BlockPos pos){
        return FluidUtil.isFluidPlaceable(world,pos,fluidBlock.getFluid());
    }
}
