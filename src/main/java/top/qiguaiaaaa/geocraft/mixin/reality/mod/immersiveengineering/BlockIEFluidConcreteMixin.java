package top.qiguaiaaaa.geocraft.mixin.reality.mod.immersiveengineering;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockIEFluidConcrete;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDecoration;
import blusunrize.immersiveengineering.common.blocks.stone.BlockTypes_StoneDevices;
import blusunrize.immersiveengineering.common.util.IEPotions;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.property.GeoFluidProperty;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.mixin.common.BlockFluidBaseAccessor;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.IMoreRealityBlockFluidBase;

import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;

@Mixin(value = BlockIEFluidConcrete.class,remap = false)
public class BlockIEFluidConcreteMixin implements IMoreRealityBlockFluidBase<BlockIEFluidConcrete> {
    private BlockIEFluidConcrete thisBlock;
    @Inject(method = "<init>",at = @At("RETURN"),remap = false)
    public void onInit(String name, Fluid fluid, Material material, CallbackInfo ci){
        thisBlock = (BlockIEFluidConcrete) (Object)this;
    }

    @Inject(method = "func_180650_b",at = @At("HEAD"),cancellable = true,remap = false)
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidProperty.isFluidToBePhysical(thisBlock.getFluid())) return;
        ci.cancel();
        int quantaPerBlock = ((BlockFluidBaseAccessor) this).getQuantaPerBlock();
        int densityDir = ((BlockFluidBaseAccessor) this).getDensityDir();
        int meta = state.getValue(LEVEL);
        int timer = state.getValue(IEProperties.INT_16);
        int quanta = quantaPerBlock-meta;
        //混凝土固化
        if(timer >= Math.min(14, quanta)) {
            IBlockState solidState;
            if(meta >= 14)
                solidState = IEContent.blockStoneDevice.getStateFromMeta(BlockTypes_StoneDevices.CONCRETE_SHEET.getMeta());
            else if(meta >= 10)
                solidState = IEContent.blockStoneDevice.getStateFromMeta(BlockTypes_StoneDevices.CONCRETE_QUARTER.getMeta());
            else if(meta >= 6)
                solidState = IEContent.blockStoneDecorationSlabs.getStateFromMeta(BlockTypes_StoneDecoration.CONCRETE.getMeta());
            else if(meta >= 2)
                solidState = IEContent.blockStoneDevice.getStateFromMeta(BlockTypes_StoneDevices.CONCRETE_THREEQUARTER.getMeta());
            else
                solidState = IEContent.blockStoneDecoration.getStateFromMeta(BlockTypes_StoneDecoration.CONCRETE.getMeta());
            world.setBlockState(pos, solidState);
            for(EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1))))
                living.addPotionEffect(new PotionEffect(IEPotions.concreteFeet, Integer.MAX_VALUE));
            return;
        } else {
            state = state.withProperty(IEProperties.INT_16, Math.min(15, timer+1));
            world.setBlockState(pos, state);
        }
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
            world.setBlockState(pos.offset(randomFacing), state.withProperty(LEVEL, meta), Constants.BlockFlags.DEFAULT);
        }else{
            world.scheduleUpdate(pos,thisBlock,((BlockFluidBaseAccessor)this).getTickRate());
        }
    }

    @Override
    public BlockIEFluidConcrete getThis() {
        return thisBlock;
    }
}
