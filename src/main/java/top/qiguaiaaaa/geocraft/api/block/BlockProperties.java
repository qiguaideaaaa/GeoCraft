package top.qiguaiaaaa.geocraft.api.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import top.qiguaiaaaa.geocraft.block.IBlockDirt;

/**
 * 本模组的方块{@link IProperty}属性
 */
public final class BlockProperties {
    /**
     * 方块的湿度，所有实现了{@link IBlockDirt}的方块都应当有该属性
     */
    public static final PropertyInteger HUMIDITY = PropertyInteger.create("humidity",0,4);
}
