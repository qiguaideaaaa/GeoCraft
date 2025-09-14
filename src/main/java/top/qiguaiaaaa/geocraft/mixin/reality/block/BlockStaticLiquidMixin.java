package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
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
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.MoreRealityBlockLiquidPressureSearchTask;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.util.mixinapi.IVanillaFlowChecker;

import java.util.Collection;
import java.util.Random;

@Mixin(value = BlockStaticLiquid.class)
public class BlockStaticLiquidMixin extends BlockLiquid implements IVanillaFlowChecker, FluidSettable {
    private Fluid thisFluid;

    protected BlockStaticLiquidMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(Material materialIn, CallbackInfo ci) {
        this.setTickRandomly(true);
    }
    @Inject(method = "updateTick",at = @At("RETURN"))
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        if(!canFlow(worldIn,pos,state,rand)){
            if(!worldIn.isRemote && !FluidPressureSearchManager.isTaskRunning(worldIn,pos)){
                Collection<BlockPos> res = FluidPressureSearchManager.getTaskResult(worldIn,pos);
                if(res == null || res.isEmpty()){
                    sendPressureQuery(worldIn,pos,state,rand,false);
                }else {
                    IBlockState nowState =state;
                    for(BlockPos toPos:res){
                        if(!nowState.getMaterial().isLiquid()) break;
                        if(tryMoveInto(worldIn,toPos,pos,nowState)) break;
                        nowState = worldIn.getBlockState(pos);
                    }
                    nowState = worldIn.getBlockState(pos);
                    if(nowState!=state && FluidUtil.getFluid(nowState) == thisFluid){
                        sendPressureQuery(worldIn,pos,state,rand,true);
                    }
                    if(nowState!=state) return;
                }
            }
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
        BlockDynamicLiquid blockdynamicliquid = BlockLiquid.getFlowingBlock(this.material);
        IVanillaFlowChecker checker = (IVanillaFlowChecker) blockdynamicliquid;
        return checker.canFlow(worldIn,pos,state,rand);
    }
    protected void sendPressureQuery(World world,BlockPos pos,IBlockState state,Random rand,boolean directly){
        IBlockState up = world.getBlockState(pos.up());
        if(FluidUtil.getFluid(up)!=thisFluid && (directly || rand.nextInt(5) == 0)) {
            FluidPressureSearchManager.addTask(world,new MoreRealityBlockLiquidPressureSearchTask(thisFluid,state,pos));
        }
    }

    protected boolean tryMoveInto(World world,BlockPos pos,BlockPos srcPos,IBlockState myState){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR){
            int quanta = 8 -myState.getValue(BlockLiquid.LEVEL);
            int movQuanta = srcPos.getY()==pos.getY()?quanta/2:quanta;
            if(movQuanta <= 0)return false;
            quanta -=movQuanta;
            if(quanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,BlockLiquid.getStaticBlock(material).getDefaultState().withProperty(BlockLiquid.LEVEL,8-quanta));
            world.setBlockState(pos,BlockLiquid.getFlowingBlock(material).getDefaultState().withProperty(BlockLiquid.LEVEL,8-movQuanta));
            return quanta == 0;
        }else if(FluidUtil.getFluid(state) == thisFluid){
            int quanta = 8-state.getValue(BlockLiquid.LEVEL);
            int myQuanta = 8 -myState.getValue(BlockLiquid.LEVEL);
            if(quanta>=myQuanta-1) return false;
            int movQuanta = srcPos.getY()==pos.getY()?(myQuanta-quanta)/2:Math.min(8-quanta,myQuanta);
            myQuanta -=movQuanta;
            if(myQuanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,BlockLiquid.getStaticBlock(material).getDefaultState().withProperty(BlockLiquid.LEVEL,8-myQuanta));
            quanta += movQuanta;
            world.setBlockState(pos,BlockLiquid.getFlowingBlock(material).getDefaultState().withProperty(BlockLiquid.LEVEL,8-quanta));
            return myQuanta==0;
        }
        return false;
    }
    @Shadow
    private void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {}

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid == null) thisFluid = fluid;
    }
}
