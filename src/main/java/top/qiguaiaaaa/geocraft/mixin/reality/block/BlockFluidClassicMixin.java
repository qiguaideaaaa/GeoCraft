package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.mixin.common.BlockFluidBaseAccessor;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.IMoreRealityBlockFluidBase;

import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;

@Mixin(value = BlockFluidClassic.class)
public class BlockFluidClassicMixin implements IMoreRealityBlockFluidBase<BlockFluidClassic> {
    private BlockFluidClassic thisBlock;
    @Inject(method = "<init>(Lnet/minecraftforge/fluids/Fluid;Lnet/minecraft/block/material/Material;)V",at = @At("RETURN"))
    private void onInit(Fluid fluid, Material material, CallbackInfo ci){
        thisBlock = (BlockFluidClassic) (Object)this;
    }
    @Inject(method = "<init>(Lnet/minecraftforge/fluids/Fluid;Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V",at = @At("RETURN"))
    private void onInit(Fluid fluid, Material material, MapColor mapColor, CallbackInfo ci){
        thisBlock = (BlockFluidClassic) (Object)this;
    }
    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisBlock.getFluid())) return;
        ci.cancel();
        int quantaPerBlock = ((BlockFluidBaseAccessor) this).getQuantaPerBlock();
        int densityDir = ((BlockFluidBaseAccessor) this).getDensityDir();
        int meta = state.getValue(LEVEL);
        int quanta = quantaPerBlock-meta;
        BlockPos downPos = pos.up(densityDir);

        IBlockState stateBelow = world.getBlockState(downPos);
        boolean canMoveDown = this.canMoveDownTo(world, downPos);
        if(canMoveDown){
            if(isSameLiquid(stateBelow)){
                flowDown(world,pos,downPos,state,stateBelow,quanta,quantaPerBlock);
            }else if(FluidUtil.isFluid(stateBelow)){//下面密度小，交换上下液体
                FluidOperationUtil.swapFluid(world,pos,pos.down());
            }else{
                FluidOperationUtil.moveFluid(world,pos,downPos);
            }
            return;
        }
        //坡度流动模式
        if(quanta == 1){
            Set<EnumFacing> directions = this.getPossibleFlowDirections(world, pos,densityDir,quantaPerBlock);
            if(directions.isEmpty()){
                return;
            }
            EnumFacing randomFacing = (EnumFacing) directions.toArray()[rand.nextInt(directions.size())];
            world.setBlockToAir(pos);
            this.flowIntoBlockDirectly(world, pos.offset(randomFacing),state, meta);
            return;
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        Set<EnumFacing> slopeModeFlowDirections = EnumSet.noneOf(EnumFacing.class);//非Q=1坡度模式可用方向
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = world.getBlockState(pos.offset(facing));
            if(!this.canDisplaceEvenIsFluid(world,pos.offset(facing))) continue;
            int facingQuanta = FluidUtil.getFluidQuanta(world,pos.offset(facing),facingState);
            if(facingQuanta<quanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
            if(facingQuanta<quanta) slopeModeFlowDirections.add(facing);
        }
        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getQuanta));
            averageModeFlowDirections.add(new FlowChoice(quantaPerBlock,null));
            int newQuanta = quanta;
            while(averageModeFlowDirections.get(0).getQuanta()<newQuanta-1){ //向四周分配流量
                newQuanta--;
                averageModeFlowDirections.get(0).addQuanta(1);
                if(averageModeFlowDirections.get(0).getQuanta() > averageModeFlowDirections.get(1).getQuanta()){
                    Collections.swap(averageModeFlowDirections,0,1);
                }
            }
            quanta = newQuanta;
            meta = quantaPerBlock - quanta;
            if (quanta<=0) world.setBlockToAir(pos); //先更新自身状态
            else {
                state = state.withProperty(LEVEL,meta);
                world.setBlockState(pos, state, Constants.BlockFlags.SEND_TO_CLIENTS);
                world.scheduleUpdate(pos, thisBlock, ((BlockFluidBaseAccessor)this).getTickRate());
                world.notifyNeighborsOfStateChange(pos,thisBlock, false);
            }
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                flowIntoBlockDirectly(world,facingPos,state,quantaPerBlock-choice.getQuanta());
            }
        }else if(!slopeModeFlowDirections.isEmpty()){ //非Q=1坡度模式
            slopeModeFlowDirections = getPossibleFlowDirections(world,pos,slopeModeFlowDirections,densityDir,quantaPerBlock,quanta);
            if(slopeModeFlowDirections.isEmpty()){
                return;
            }
            EnumFacing randomFacing = (EnumFacing) slopeModeFlowDirections.toArray()[rand.nextInt(slopeModeFlowDirections.size())];
            int newLiquidQuanta = quanta-1;
            int newLiquidMeta = quantaPerBlock-newLiquidQuanta;
            //更新自己
            state = state.withProperty(LEVEL,newLiquidMeta);
            world.setBlockState(pos,state,Constants.BlockFlags.SEND_TO_CLIENTS);
            world.scheduleUpdate(pos,thisBlock,((BlockFluidBaseAccessor)this).getTickRate());
            world.notifyNeighborsOfStateChange(pos,thisBlock,false);
            //移动至新位置
            world.setBlockState(pos.offset(randomFacing), state.withProperty(LEVEL, meta), Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }

    @Inject(method = "drain",at = @At("HEAD"),cancellable = true,remap = false)
    private void drain(World world, BlockPos pos, boolean doDrain, CallbackInfoReturnable<FluidStack> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisBlock.getFluid())) return;
        final FluidStack fluidStack = new FluidStack(thisBlock.getFluid(), MathHelper.floor((thisBlock.getQuantaPercentage(world, pos) * Fluid.BUCKET_VOLUME)));

        if (doDrain) {
            world.setBlockToAir(pos);
        }
        cir.setReturnValue(fluidStack);
        cir.cancel();
    }
    //下面这段代码参考自Forge的BlockFluidFinite类
    @Inject(method = "place",at =@At("HEAD"),cancellable = true,remap = false)
    private void place(World world, BlockPos pos, FluidStack fluidStack, boolean doPlace, CallbackInfoReturnable<Integer> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisBlock.getFluid())) return;
        cir.cancel();
        IBlockState existing = world.getBlockState(pos);
        float amountPerQuanta = Fluid.BUCKET_VOLUME / ((BlockFluidBaseAccessor)this).getQuantaPerBlockFloat();

        int amountInFact = Fluid.BUCKET_VOLUME;
        int quantaExpected = ((BlockFluidBaseAccessor)this).getQuantaPerBlock();
        if (fluidStack.amount < amountInFact) {
            amountInFact = MathHelper.floor(amountPerQuanta * MathHelper.floor(fluidStack.amount / amountPerQuanta));
            quantaExpected = MathHelper.floor(amountInFact / amountPerQuanta);
        }
        if (existing.getBlock() == thisBlock) {
            int existingQuanta = FluidUtil.getFluidQuanta(world,pos,existing);
            int missingQuanta = ((BlockFluidBaseAccessor)this).getQuantaPerBlock() - existingQuanta;
            amountInFact = Math.min(amountInFact, MathHelper.floor(missingQuanta * amountPerQuanta));
            quantaExpected = Math.min(quantaExpected + existingQuanta,((BlockFluidBaseAccessor)this).getQuantaPerBlock());
        }

        if (quantaExpected < 1 || quantaExpected > 16)
            cir.setReturnValue(0);

        if (doPlace) {
            net.minecraftforge.fluids.FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            world.setBlockState(pos,thisBlock.getDefaultState().withProperty(LEVEL, ((BlockFluidBaseAccessor)this).getQuantaPerBlock()-quantaExpected), Constants.BlockFlags.SEND_TO_CLIENTS);
        }

        cir.setReturnValue(amountInFact);
    }

    @Inject(method = "canDrain",at = @At("HEAD"),cancellable = true,remap = false)
    private void canDrain(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisBlock.getFluid())) return;
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Override
    public BlockFluidClassic getThis() {
        return thisBlock;
    }
}
