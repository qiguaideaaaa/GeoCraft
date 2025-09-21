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
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure.RealityPressureTaskBuilder;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.util.mixinapi.IVanillaFlowChecker;

import java.util.Collection;
import java.util.Random;

@Mixin(value = BlockStaticLiquid.class)
public class BlockStaticLiquidMixin extends BlockLiquid implements IVanillaFlowChecker, FluidSettable {
    private static final boolean debug = false;
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
        if(worldIn.isRemote) return;
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        if(!canFlow(worldIn,pos,state,rand)){
            Collection<BlockPos> res = FluidPressureSearchManager.getTaskResult(worldIn,pos);

            if(res == null || res.isEmpty()){
                sendPressureQuery(worldIn,pos,state,rand,false);
                if(debug) GeoCraft.getLogger().info("{}: no res,send query",pos);
            }else {
                IBlockState nowState =state;
                if(debug) GeoCraft.getLogger().info("{}: has res :",pos);
                for(BlockPos toPos:res){
                    if(!nowState.getMaterial().isLiquid()) break;
                    if(tryMoveInto(worldIn,toPos,pos,nowState)) break;
                    nowState = worldIn.getBlockState(pos);
                    if(debug) GeoCraft.getLogger().info("{} now State: {}",toPos,nowState);
                }

                nowState = worldIn.getBlockState(pos);
                if(nowState!=state && FluidUtil.getFluid(nowState) == thisFluid){
                    sendPressureQuery(worldIn,pos,nowState,rand,true);
                }else if(nowState == state){
                    sendPressureQuery(worldIn,pos,state,rand,false);
                }
                if(nowState!=state) return;
            }
            IBlockState newState = EventFactory.afterBlockLiquidStaticUpdate(thisFluid,worldIn,pos,state);
            if(newState != null){
                worldIn.setBlockState(pos,newState);
                return;
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
        if(FluidPressureSearchManager.isTaskRunning(world,pos)){
            if(debug) GeoCraft.getLogger().info("{}: task running, returned",pos);
            return;
        }
        IBlockState up = world.getBlockState(pos.up());
        if(FluidUtil.getFluid(up)==thisFluid){
            if(up.getValue(LEVEL)==0){
                if(debug) GeoCraft.getLogger().info("{}: up is full water, returned",pos);
                return;
            }
        }
        if(directly || rand.nextInt(5) <2) {
            if(debug){
                FluidPressureSearchManager.addTask(world,RealityPressureTaskBuilder.createVanillaTask_Debug(thisFluid,state,pos,BaseUtil.getRandomPressureSearchRange()));
                return;
            }
            FluidPressureSearchManager.addTask(world,
                    RealityPressureTaskBuilder.createVanillaTask(thisFluid,state,pos, BaseUtil.getRandomPressureSearchRange())
            );
        }
    }

    protected boolean tryMoveInto(World world,BlockPos toPos,BlockPos srcPos,IBlockState myState){
        if(!world.isBlockLoaded(toPos)) return false;
        IBlockState toState = world.getBlockState(toPos);
        if(toState.getMaterial() == Material.AIR){
            int quanta = 8 -myState.getValue(LEVEL);
            int movQuanta = srcPos.getY()==toPos.getY()?quanta/2:quanta;
            if(movQuanta <= 0)return false;
            quanta -=movQuanta;
            if(quanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,this.getDefaultState().withProperty(LEVEL,8-quanta));
            world.setBlockState(toPos,this.getDefaultState().withProperty(LEVEL,8-movQuanta));
            return quanta == 0;
        }else if(FluidUtil.getFluid(toState) == thisFluid){
            int toQuanta = 8-toState.getValue(LEVEL);
            int myQuanta = 8 -myState.getValue(LEVEL);
            if(toPos.getY() == srcPos.getY() && toQuanta>=myQuanta-1) return false;
            int movQuanta = srcPos.getY()==toPos.getY()?(myQuanta-toQuanta)/2:Math.min(8-toQuanta,myQuanta);
            myQuanta -=movQuanta;
            if(myQuanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,this.getDefaultState().withProperty(LEVEL,8-myQuanta));
            toQuanta += movQuanta;
            world.setBlockState(toPos,this.getDefaultState().withProperty(LEVEL,8-toQuanta));
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
