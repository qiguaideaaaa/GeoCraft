package top.qiguaiaaaa.fluidgeography.mixin.reality.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.fluidgeography.util.mixinapi.IVanillaFlowChecker;
import top.qiguaiaaaa.fluidgeography.mixin.common.BlockAccessor;

import java.util.*;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;

@Mixin(value = BlockStaticLiquid.class)
public class BlockStaticLiquidMixin implements IVanillaFlowChecker, FluidSettable {
    private Block thisBlock;
    private Fluid thisFluid;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(Material materialIn, CallbackInfo ci) {
        thisBlock = (Block)(Object)this;
        thisBlock.setTickRandomly(true);
    }
    @Inject(method = "updateTick",at = @At("RETURN"))
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(fluidsNotToSimulate.containsEquivalent(thisFluid)) return;
        if(!canFlow(worldIn,pos,state,rand)){
            IBlockState newState = EventFactory.afterBlockLiquidStaticUpdate(thisFluid,worldIn,pos,state);
            if(newState != null){
                worldIn.setBlockState(pos,newState);
            }
            return;
        }
        updateLiquid(worldIn,pos,state);
    }
    @Override
    public boolean canFlow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        BlockDynamicLiquid blockdynamicliquid = BlockLiquid.getFlowingBlock(((BlockAccessor)this).getMaterial());
        IVanillaFlowChecker checker = (IVanillaFlowChecker) blockdynamicliquid;
        return checker.canFlow(worldIn,pos,state,rand);
    }
    @Shadow
    private void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {}

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid == null) thisFluid = fluid;
    }
}
