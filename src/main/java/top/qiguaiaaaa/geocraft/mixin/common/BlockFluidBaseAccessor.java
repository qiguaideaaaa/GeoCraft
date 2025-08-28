package top.qiguaiaaaa.geocraft.mixin.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BlockFluidBase.class,remap = false)
public interface BlockFluidBaseAccessor {
    @Accessor(value = "quantaPerBlock",remap = false)
    int getQuantaPerBlock();

    @Accessor(value = "quantaPerBlockFloat",remap = false)
    float getQuantaPerBlockFloat();

    @Accessor(value = "tickRate",remap = false)
    int getTickRate();

    @Accessor(value = "densityDir",remap = false)
    int getDensityDir();

    @Invoker(value = "hasVerticalFlow",remap = false)
    boolean hasVerticalFlowR(IBlockAccess world, BlockPos pos);
}
