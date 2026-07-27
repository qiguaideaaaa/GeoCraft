/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package 清汩萌.天圆地方.世界;

import com.google.common.annotations.Beta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.世界.光照.光照盒;
import 清汩萌.天圆地方.世界.光照.无光光照盒;
import 清汩萌.天圆地方.世界.沙盒.沙盒;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author QiguaiAAAA
 */
@Beta
public class 模拟沙盒世界 extends 模拟世界 {

    protected 沙盒 $沙盒;
    protected 光照盒 lightGridSky = 无光光照盒.SKY;
    protected 光照盒 lightGridBlock = 无光光照盒.BLOCK;
    protected IBlockState $空气方块;

    protected 模拟沙盒世界(final @Nonnull WorldInfo info,
                           final @Nonnull WorldProvider providerIn,
                           final @Nonnull Profiler profilerIn,
                           final boolean client) {
        super(info, providerIn, profilerIn, client);
    }

    @Nonnull
    public static 模拟沙盒世界 构建(final @Nonnull WorldInfo info, final boolean isClient){
        return 构建(info,new MockWorldProvider(),isClient);
    }

    @Nonnull
    public static 模拟沙盒世界 构建(final @Nonnull WorldInfo info, final @Nonnull MockWorldProvider provider, final boolean isClient){
        return new 模拟沙盒世界(
                info,
                provider,
                new Profiler(),
                isClient
        );
    }

    public void 配置沙盒(final @Nonnull 沙盒 $沙盒){
        this.$沙盒 = $沙盒;
    }

    public void 配置天空光照盒(final @Nonnull 光照盒 $光照盒) {
        Assertions.assertEquals(EnumSkyBlock.SKY, $光照盒.getType());
        this.lightGridSky = $光照盒;
    }

    public void 配置方块光照盒(final @Nonnull 光照盒 $光照盒) {
        Assertions.assertEquals(EnumSkyBlock.BLOCK, $光照盒.getType());
        this.lightGridBlock = $光照盒;
    }

    public void 配置空气方块(final @Nonnull IBlockState $块) {
        this.$空气方块 = $块;
    }

    public final @Nonnull 沙盒 获取沙盒() {
        return $沙盒;
    }

    @Nonnull
    @Override
    protected IChunkProvider createChunkProvider() {
        return Assertions.fail("模拟沙盒世界不支持 IChunkProvider");
    }

    @Override
    public boolean isAirBlock(final @Nonnull BlockPos pos) {
        return $沙盒.isAirBlock(pos);
    }

    @Override
    protected boolean isChunkLoaded(final int x,final int z,final boolean allowEmpty) {
        return true;
    }

    @Override
    public boolean setBlockState(final @Nonnull BlockPos pos,final @Nonnull IBlockState newState,final int flags) {
        if($沙盒.isOutOfRange(pos)) return false;
        final @Nonnull IBlockState oldState = $沙盒.setBlockState(pos,newState);
        this.markAndNotifyBlock(pos, null, oldState, newState, flags);
        return true;
    }

    @Override
    public boolean setBlockToAir(final @Nonnull BlockPos pos) {
        Assertions.assertNotNull($空气方块);
        return this.setBlockState(pos, $空气方块);
    }

    @Override
    public boolean canSeeSky(@Nonnull final BlockPos pos) {
        return $沙盒.canSeeSky(pos);
    }

    @Override
    public int getLight(final @Nonnull BlockPos pos) {
        return Math.max(lightGridSky.getLight(pos),lightGridBlock.getLight(pos));
    }

    @Override
    public int getLight(@Nonnull final BlockPos pos,final boolean checkNeighbors) {
        if (checkNeighbors && this.getBlockState(pos).useNeighborBrightness()) {
            final int up = this.getLight(pos.up(), false);
            final int east = this.getLight(pos.east(), false);
            final int west = this.getLight(pos.west(), false);
            final int south = this.getLight(pos.south(), false);
            final int north = this.getLight(pos.north(), false);
            return Math.max(up,Math.max(east,Math.max(west,Math.max(south,north))));
        } else{
            return getLight(pos);
        }
    }

    @Override
    public int getHeight(final int x,final int z) {
        return Assertions.fail("TO DO"); // TODO : support getHeight()
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getChunksLowestHorizon(final int x,final int z) {
        return Assertions.fail("Deprecated"); // TODO: support this
    }

    @Override
    @Deprecated
    public int getLightFromNeighborsFor(final @Nonnull EnumSkyBlock type, final @Nonnull BlockPos pos) {
        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        } else if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        } else if (this.getBlockState(pos).useNeighborBrightness()) {
            final int up = this.getLightFor(type,pos.up());
            final int east = this.getLightFor(type,pos.east());
            final int west = this.getLightFor(type,pos.west());
            final int south = this.getLightFor(type,pos.south());
            final int north = this.getLightFor(type,pos.north());
            return Math.max(up,Math.max(east,Math.max(west,Math.max(south,north))));
        } else return getLightFor(type,pos);
    }

    @Override
    public int getLightFor(@Nonnull final EnumSkyBlock type,final @Nonnull BlockPos pos) {
        switch (type){
            case SKY:return lightGridSky.getLight(pos);
            case BLOCK:return lightGridBlock.getLight(pos);
            default:return Assertions.fail("NULL Light TYPE!");
        }
    }

    @Override
    public void setLightFor(@Nonnull final EnumSkyBlock type,final @Nonnull BlockPos pos,final int lightValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCombinedLight(@Nonnull final BlockPos pos,final int lightValue) {
        return $沙盒.getCombinedLight(pos,lightValue);
    }

    @Nonnull
    @Override
    public IBlockState getBlockState(final @Nonnull BlockPos pos) {
        return $沙盒.getBlockState(pos);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(final @Nonnull BlockPos pos) {
        return $沙盒.getTileEntity(pos);
    }

    @Override
    public int getStrongPower(final @Nonnull BlockPos pos,final @Nonnull EnumFacing direction) {
        return $沙盒.getStrongPower(pos,direction);
    }

    @Override
    public boolean isSideSolid(@Nonnull final BlockPos pos,final @Nonnull EnumFacing side,final boolean _default) {
        return $沙盒.isSideSolid(pos,side,_default);
    }
}
