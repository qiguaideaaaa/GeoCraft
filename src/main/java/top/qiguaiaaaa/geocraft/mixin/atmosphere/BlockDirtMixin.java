package top.qiguaiaaaa.geocraft.mixin.atmosphere;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.simulation.SimulationMode;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.mixin.common.BlockAccessor;

import java.util.Random;

import static net.minecraft.block.BlockDirt.SNOWY;
import static net.minecraft.block.BlockDirt.VARIANT;
import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.DIRT_HUMIDITY;

@Mixin(value = BlockDirt.class)
public class BlockDirtMixin {
    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        ((Block)(BlockAccessor)this).setTickRandomly(true);
        ((BlockAccessor)this).setDefaultState(((BlockAccessor)this).getContainer().getBaseState().
                withProperty(VARIANT, BlockDirt.DirtType.DIRT)
                .withProperty(SNOWY, Boolean.FALSE)
                .withProperty(DIRT_HUMIDITY, 0));
    }
    @Inject(method = "getStateFromMeta",at = @At(value = "HEAD"),cancellable = true)
    private void getStateFromMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        cir.cancel();
        cir.setReturnValue(((BlockAccessor)this).getDefaultState()
                .withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta%3))
                .withProperty(DIRT_HUMIDITY,Math.min(meta/3,4)));
    }
    @Inject(method = "createBlockState",at = @At(value = "HEAD"),cancellable = true)
    private void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer((Block) ((BlockAccessor)this), VARIANT, SNOWY,DIRT_HUMIDITY));
    }

    public void func_180645_a(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if(random.nextInt(3) != 1) return;
        int humidity = state.getValue(DIRT_HUMIDITY);
        if(humidity == 4) return;
        BlockPos upPos = pos.up();
        IBlockState upState = worldIn.getBlockState(upPos);
        if(FluidUtil.getFluid(upState) == FluidRegistry.WATER){
            if(SimulationConfig.SIMULATION_MODE.getValue() == SimulationMode.MORE_REALITY){
                int meta = upState.getValue(BlockLiquid.LEVEL);
                if(meta ==7){
                    worldIn.setBlockToAir(upPos);
                }else{
                    worldIn.setBlockState(upPos,upState.withProperty(BlockLiquid.LEVEL,meta+1));
                }
            }
            worldIn.setBlockState(pos,state.withProperty(DIRT_HUMIDITY,humidity+1));
        }
    }

    /**
     * {@link Block#onPlayerDestroy(World, BlockPos, IBlockState)}
     */
    public void func_176206_d(World worldIn, BlockPos pos, IBlockState state) {
        dropWater(worldIn, pos, state);
    }

    public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
        if (((Block)(BlockAccessor)this).hasTileEntity(state) && !(Blocks.DIRT instanceof BlockContainer)) {
            worldIn.removeTileEntity(pos);
        }
        dropWater(worldIn, pos, state);
    }

    public void dropWater(World world,BlockPos pos,IBlockState state){
        int humidity = state.getValue(DIRT_HUMIDITY);
        if(humidity == 0) return;
        if(SimulationConfig.SIMULATION_MODE.getValue() != SimulationMode.MORE_REALITY) return;
        world.setBlockState(pos, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-humidity),3);
    }
}
