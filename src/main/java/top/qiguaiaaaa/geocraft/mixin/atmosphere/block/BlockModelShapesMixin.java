package top.qiguaiaaaa.geocraft.mixin.atmosphere.block;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSand;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
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
public class BlockModelShapesMixin {
    @Final
    @Shadow
    private BlockStateMapper blockStateMapper;

    @Inject(method = "registerBlockWithStateMapper",at = @At("HEAD"),cancellable = true)
    public void registerBlockWithStateMapper(Block assoc, IStateMapper stateMapper, CallbackInfo ci) {
        if(assoc == Blocks.DIRT){
            ci.cancel();
            stateMapper = new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    Map<IProperty<?>, Comparable<? >> map = Maps.newLinkedHashMap(state.getProperties());
                    String s = BlockDirt.VARIANT.getName((BlockDirt.DirtType)map.remove(BlockDirt.VARIANT));

                    if (BlockDirt.DirtType.PODZOL != state.getValue(BlockDirt.VARIANT)) {
                        map.remove(BlockDirt.SNOWY);
                    }
                    map.remove(BlockProperties.HUMIDITY);

                    return new ModelResourceLocation(s, this.getPropertyString(map));
                }
            };
            this.blockStateMapper.registerBlockStateMapper(assoc, stateMapper);
        }else if(assoc == Blocks.SAND){
            ci.cancel();
            stateMapper = new StateMap.Builder().withName(BlockSand.VARIANT).ignore(BlockProperties.HUMIDITY).build();
            this.blockStateMapper.registerBlockStateMapper(assoc, stateMapper);
        }

    }
    @Inject(method = "registerAllBlocks",at = @At("TAIL"))
    private void registerAllBlocks(CallbackInfo ci){
        this.blockStateMapper.registerBlockStateMapper(Blocks.GRASS, (new StateMap.Builder())
                .ignore(BlockProperties.HUMIDITY)
                .build());
    }
}
