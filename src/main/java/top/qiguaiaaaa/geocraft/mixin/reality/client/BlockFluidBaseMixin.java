package top.qiguaiaaaa.geocraft.mixin.reality.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

/**
 * @author QiguaiAAAA
 */
@Mixin(value = BlockFluidBase.class)
public abstract class BlockFluidBaseMixin extends Block {
    @Final
    @Shadow(remap = false)
    protected String fluidName;

    @Shadow(remap = false)
    protected int densityDir;

    public BlockFluidBaseMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Inject(method = "shouldSideBeRendered",at = @At("HEAD"),cancellable = true)
    public void shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        IBlockState neighbor = world.getBlockState(pos.offset(side));
        Fluid neighborFluid = FluidUtil.getFluid(neighbor);

        if(neighborFluid != null){
            if(neighborFluid.getName().equals(fluidName)){
                cir.setReturnValue(false);
                return;
            }
            cir.setReturnValue(true);
            return;
        }
        if (side == (densityDir <0 ? EnumFacing.UP : EnumFacing.DOWN)) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(super.shouldSideBeRendered(state, world, pos, side));
    }

}
