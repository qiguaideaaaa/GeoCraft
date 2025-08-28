package top.qiguaiaaaa.geocraft.mixin.atmosphere;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.block.BlockProperties;

import java.util.Map;

@Mixin(BlockModelShapes.class)
public class BlockDirtRender {
    @Final
    @Shadow
    private final BlockStateMapper blockStateMapper = new BlockStateMapper();

    @Inject(method = "registerBlockWithStateMapper",at = @At("HEAD"),cancellable = true)
    public void registerBlockWithStateMapper(Block assoc, IStateMapper stateMapper, CallbackInfo ci) {
        if(assoc != Blocks.DIRT) return;
        ci.cancel();
        stateMapper = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                Map<IProperty<?>, Comparable<? >> map = Maps.newLinkedHashMap(state.getProperties());
                String s = BlockDirt.VARIANT.getName((BlockDirt.DirtType)map.remove(BlockDirt.VARIANT));

                if (BlockDirt.DirtType.PODZOL != state.getValue(BlockDirt.VARIANT)) {
                    map.remove(BlockDirt.SNOWY);
                }
                map.remove(BlockProperties.DIRT_HUMIDITY);

                return new ModelResourceLocation(s, this.getPropertyString(map));
            }
        };
        this.blockStateMapper.registerBlockStateMapper(assoc, stateMapper);
    }
}
