/*
 * Copyright 2026 QGMoe
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
 * 版权所有 2026 QGMoe
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

package moe.qingu.geocraft.world.scheduler;

import moe.qingu.geocraft.api.world.tick.IScheduledTick;
import moe.qingu.geocraft.api.world.tick.TickPriority;
import moe.qingu.nickel.nbt.operation.SNBTFunction;
import moe.qingu.nickel.nbt.operation.SNBTOperations;
import net.minecraft.block.Block;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.原料.方块原料;
import 清汩萌.造.空间.空间工具;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.词块.词块;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class 可测试的计划刻 implements IScheduledTick {
    private static boolean $初始化;
    public int[] $于 = new int[]{0,0,0};
    public String $块;
    public long $时;
    public 计划刻等级 $级 = 计划刻等级.中上7;

    private BlockPos $位置;
    private Block $方块;
    private 词块 $词块;

    public static void 加载SNBT辅助函数(){
        if($初始化) return;
        SNBTOperations.loadFuncs(可测试的计划刻.class);
        $初始化 = true;
    }

    public void 初始化(final @Nonnull 空间构造器 $空间构造器,final @Nonnull int[] offset,final boolean $网格坐标){
        if($网格坐标) $于 = 空间工具.转换为游戏坐标($于);
        for(int i=0;i<3;i++) $于[i] = Math.addExact($于[i],offset[i]);
        this.$位置 = new BlockPos($于[0],$于[1],$于[2]);
        $方块 = $空间构造器.进行映射(词块.of($块)).getBlock();
        this.$词块 = $空间构造器.进行映射($方块.getDefaultState());
    }

    @Nonnull
    @Override
    public BlockPos pos() {
        return $位置;
    }

    @Nonnull
    @Override
    public Block block() {
        return $方块;
    }

    @Override
    public long triggeredTick() {
        return $时;
    }

    @Nonnull
    @Override
    public TickPriority priority() {
        return $级.as;
    }

    @Override
    @SuppressWarnings("EqualsDoesntCheckParameterClass")
    public boolean equals(final Object obj) {
        return IScheduledTick.equals(this,obj);
    }

    @Override
    public int hashCode() {
        return $位置.hashCode();
    }

    @Override
    public String toString() {
        return "#" + $词块 + '[' +
                "x:" + $于[0] + ',' +
                "y:" + $于[1] + ',' +
                "z:" + $于[2] + ',' +
                "p:" + $级 + ',' +
                "t:" + $时 + ']';
    }

    public static boolean 严格相等(final @Nonnull IScheduledTick a,final @Nonnull IScheduledTick b){
        return a.block() == b.block() && a.pos().equals(b.pos()) && a.triggeredTick() == b.triggeredTick() && a.priority() == b.priority();
    }

    /*  ---------------------------
             SNBT 操作区
        --------------------------- */

    @Nonnull
    @SNBTFunction(name = "blockOf")
    @SuppressWarnings("unused")
    public static NBTTagInt 标签化方块(final @Nonnull NBTTagString block){
        return new NBTTagInt(Block.REGISTRY.getIDForObject(方块原料._全构造器_.进行映射(词块.of(block.getString())).getBlock()));
    }

    @Nonnull
    @SNBTFunction(name = "tick")
    @SuppressWarnings("unused")
    public static NBTTagLong 标签化计划刻(final @Nonnull NBTTagInt delay,
                                          final @Nonnull NBTTagByte priority,
                                          final @Nonnull NBTTagIntArray posArr,
                                          final @Nonnull NBTTagString block){
        long res = 0L;
        Assertions.assertTrue(priority.getInt()>=0,priority.getInt()+" is too small");
        Assertions.assertTrue(priority.getInt()<16,priority.getInt()+"is too large");
        res |= Integer.toUnsignedLong(delay.getInt()) << 32;
        res |= Byte.toUnsignedLong(priority.getByte()) << 28;
        final int[] pos = posArr.getIntArray();
        Validate.inclusiveBetween(0,15,pos[0]);
        Validate.inclusiveBetween(0,255,pos[1]);
        Validate.inclusiveBetween(0,15,pos[2]);
        res |= Integer.toUnsignedLong(pos[1])<<20;
        res |= Integer.toUnsignedLong(pos[2])<<16;
        res |= Integer.toUnsignedLong(pos[0])<<12;
        res |= Integer.toUnsignedLong(Block.REGISTRY.getIDForObject(方块原料._全构造器_.进行映射(词块.of(block.getString())).getBlock()));
        return new NBTTagLong(res);
    }

    @Nonnull
    @SNBTFunction(name = "packPosAndPri")
    @SuppressWarnings("unused")
    public static NBTTagLong 打包的坐标和优先级(final NBTTagByte priority,final @Nonnull NBTTagIntArray posArr){
        long res = 0L;
        Assertions.assertTrue(priority.getInt()>=0,priority.getInt()+" is too small");
        Assertions.assertTrue(priority.getInt()<16,priority.getInt()+"is too large");
        res |= Byte.toUnsignedLong(priority.getByte()) << 40;
        final int[] pos = posArr.getIntArray();
        Validate.inclusiveBetween(0,15,pos[0]);
        Validate.inclusiveBetween(0,15,pos[2]);
        res |= Integer.toUnsignedLong(pos[0])<<36;
        res |= Integer.toUnsignedLong(pos[2])<<32;
        res |= Integer.toUnsignedLong(pos[1]);
        return new NBTTagLong(res);
    }

    /*  ---------------------------
             Setter 用于 YAML
        --------------------------- */

    @Deprecated
    public void setX(final int x) {
        this.$于[0] = x;
    }

    @Deprecated
    public void setY(final int y) {
        this.$于[1] = y;
    }

    @Deprecated
    public void setZ(final int z) {
        this.$于[2] = z;
    }

    @SuppressWarnings("unused")
    public void set于(final @Nonnull int[] $于) {
        this.$于 = $于;
    }

    @SuppressWarnings("unused")
    public void set块(final @Nonnull String $块) {
        this.$块 = $块;
    }

    @SuppressWarnings("unused")
    public void set时(final long $时) {
        this.$时 = $时;
    }

    @SuppressWarnings("unused")
    public void set级(final @Nonnull 计划刻等级 $级) {
        this.$级 = $级;
    }
}
