package top.qiguaiaaaa.geocraft.mixin.groundwater.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.block.IBlockDirt;

import javax.annotation.Nonnull;
import java.util.Random;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockSand.class)
public class BlockSandMixin extends BlockFalling implements IBlockDirt {
    @Shadow @Final public static PropertyEnum<BlockSand.EnumType> VARIANT;

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSand.EnumType.SAND).withProperty(HUMIDITY,0));
    }
    @Inject(method = "getStateFromMeta",at = @At(value = "HEAD"),cancellable = true)
    public void getStateFromMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        cir.cancel();
        if(meta>=10) cir.setReturnValue(this.getDefaultState());
        cir.setReturnValue(this.getDefaultState().withProperty(VARIANT,BlockSand.EnumType.byMetadata(meta%2)).withProperty(HUMIDITY,meta/2));
    }
    @Inject(method = "getMetaFromState",at = @At(value = "HEAD"),cancellable = true)
    public void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(VARIANT).getMetadata()+state.getValue(HUMIDITY)*2);
    }

    @Inject(method = "createBlockState",at = @At(value = "HEAD"),cancellable = true)
    protected void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, VARIANT,HUMIDITY));
    }

    @Override
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        this.onRandomTick(worldIn, pos, state, random);
    }

    @Override
    public void onPlayerDestroy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Override
    public int getMaxStableHumidity(IBlockState state) {
        return 1;
    }
}
