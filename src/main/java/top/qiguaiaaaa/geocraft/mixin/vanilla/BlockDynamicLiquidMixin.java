package top.qiguaiaaaa.geocraft.mixin.vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.mixin.common.BlockLiquidAccessor;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static top.qiguaiaaaa.geocraft.configs.SimulationConfig.*;

@Mixin(value = BlockDynamicLiquid.class)
public class BlockDynamicLiquidMixin implements FluidSettable {
    @Shadow
    int adjacentSourceBlocks;
    private Fluid thisFluid;
    private Block thisBlock;
    private Material thisMaterial;
    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(Material materialIn, CallbackInfo ci) {
        thisBlock = (Block)(Object)this;
        thisMaterial = materialIn;
    }
    /**
     * @author QiguaiAAAA
     * @reason No
     */
    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        ci.cancel();
        if (!worldIn.isAreaLoaded(pos, this.getSlopeFindDistance(worldIn))) return;
        int liquidMeta = state.getValue(LEVEL);
        int spreadLevel = getSpreadLevel(worldIn);

        int updateRate = thisBlock.tickRate(worldIn);

        IBlockState stateBelow = worldIn.getBlockState(pos.down());

        //是否能够往下流
        Optional<BlockPos> sourcePosOption = Optional.empty();
        if(liquidMeta == 0) sourcePosOption = Optional.of(pos);
        boolean canMoveSourceDown = this.canMoveInto(worldIn, pos.down(), stateBelow);
        if(canMoveSourceDown){
            if (!sourcePosOption.isPresent())
                sourcePosOption = FluidSearchUtil.findSource(worldIn,pos,thisMaterial,false,false,
                        findSourceMaxIterationsWhenVerticalFlowing.getValue(),
                        findSourceMaxSameLevelIterationsWhenVerticalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(worldIn,sourcePosOption.get(),pos.down());
                if(sourcePosOption.get() == pos) return;
            }
        }else if(liquidMeta == 1){
            sourcePosOption = FluidSearchUtil.findSource(worldIn,pos,thisMaterial,true,false,
                    findSourceMaxIterationsWhenHorizontalFlowing.getValue(),
                    findSourceMaxSameLevelIterationsWhenHorizontalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(worldIn,sourcePosOption.get(),pos);
                worldIn.scheduleUpdate(pos,BlockLiquid.getStaticBlock(thisMaterial),updateRate);
                return;
            }
        }

        boolean noSourceFound = canMoveSourceDown && !sourcePosOption.isPresent();

        if (liquidMeta > 0) {
            //水平方向处理
            int 相邻方块最高等级 = -100;
            this.adjacentSourceBlocks = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                相邻方块最高等级 = this.checkAdjacentBlock(worldIn, pos.offset(enumfacing), 相邻方块最高等级);

            int newLiquidMeta = 相邻方块最高等级 + spreadLevel;

            if (newLiquidMeta >= 8 || 相邻方块最高等级 < 0) newLiquidMeta = -1;
            //垂直方向处理
            int upBlockMeta = ((BlockLiquidAccessor)this).getDepth(worldIn.getBlockState(pos.up()));

            if (upBlockMeta >= 0) {
                if (upBlockMeta >= 8) newLiquidMeta = upBlockMeta;
                else newLiquidMeta = upBlockMeta + 8;
            }
            //无限水
            if(enableInfiniteWater.getValue()){
                if (this.adjacentSourceBlocks >= 2 && ForgeEventFactory.canCreateFluidSource(worldIn, pos, state, thisMaterial == Material.WATER)) {
                    IBlockState iblockstate = worldIn.getBlockState(pos.down());

                    if (iblockstate.getMaterial().isSolid()) {
                        newLiquidMeta = 0;
                    } else if (iblockstate.getMaterial() == thisMaterial && iblockstate.getValue(LEVEL) == 0) {
                        newLiquidMeta = 0;
                    }
                }
            }
            boolean isQuantaDecreasing = newLiquidMeta < 8 && newLiquidMeta > liquidMeta;
            //岩浆处理
            if (!noSourceFound && (thisMaterial == Material.LAVA) && isQuantaDecreasing && rand.nextInt(4) != 0){
                updateRate *= 4;
            }

            //更新纹理（流动还是静止）
            if (newLiquidMeta == liquidMeta) {
                this.placeStaticBlock(worldIn, pos, state);
            } else {
                liquidMeta = newLiquidMeta;
                if (newLiquidMeta < 0) worldIn.setBlockToAir(pos);
                else {
                    state = state.withProperty(LEVEL, newLiquidMeta);
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos, (Block)(Object)this, updateRate);
                    worldIn.notifyNeighborsOfStateChange(pos, (Block)(Object)this, false);
                }
            }
        } else {
            this.placeStaticBlock(worldIn, pos, state);
        }
        if(liquidMeta <0) return;
        stateBelow = worldIn.getBlockState(pos.down());
        if (canFlowInto(worldIn, pos.down(), stateBelow)) {
            if (thisMaterial == Material.LAVA && stateBelow.getMaterial() == Material.WATER) {
                worldIn.setBlockState(pos.down(), ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos.down(), pos, Blocks.STONE.getDefaultState()));
                FluidOperationUtil.triggerFluidMixEffects(worldIn, pos.down());
                return;
            }
            if (liquidMeta >= 8) this.tryFlowInto(worldIn, pos.down(), stateBelow, liquidMeta);
            else this.tryFlowInto(worldIn, pos.down(), stateBelow, liquidMeta + 8);
        } else if (FluidUtil.isFullFluid(worldIn,pos.down(),stateBelow) || this.isBlocked(worldIn, pos.down(), stateBelow)){//横向流动
            Set<EnumFacing> directions = this.getPossibleFlowDirections(worldIn, pos);
            int nextLiquidState = liquidMeta + spreadLevel;

            if (liquidMeta >= 8) nextLiquidState = 1;
            if (nextLiquidState >= 8) return;

            for (EnumFacing facing : directions)
                this.tryFlowInto(worldIn, pos.offset(facing), worldIn.getBlockState(pos.offset(facing)), nextLiquidState);
        }
    }
    private boolean canMoveInto(World worldIn,BlockPos pos,IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material != thisMaterial) return false;
            return state.getValue(LEVEL) != 0;
        }
        return !this.isBlocked(worldIn,pos,state);
    }
    private int getSpreadLevel(World world){
        if (thisMaterial == Material.LAVA && !world.provider.doesWaterVaporize()) {
            return 2;
        }
        return 1;
    }
    @Shadow
    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {}
    @Shadow
    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {return false;}
    @Shadow
    private int getSlopeFindDistance(World worldIn) {
        return 0;
    }
    @Shadow
    protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel){return 0;}
    @Shadow
    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState){}
    @Shadow
    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {return null;}
    @Shadow
    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state){return false;}

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        thisFluid = fluid;
    }

}
