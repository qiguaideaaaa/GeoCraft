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

package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.geocraft.util.math.Int10;
import top.qiguaiaaaa.geocraft.util.math.vec.IVec3i;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static top.qiguaiaaaa.geocraft.util.math.vec.IVec3i.X_INT_OFFSET;
import static top.qiguaiaaaa.geocraft.util.math.vec.IVec3i.Y_INT_OFFSET;

/**
 * @author QiguaiAAAA
 */
public class FluidPressureSearchTaskSmallRangeRelativeResult implements IFluidPressureSearchTaskResult{
    private static final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(); //仅Minecraft 服务器线程使用
    protected final int cx, cy, cz;
    protected final IntList res = new IntArrayList();
    protected int curIndex = 0;

    public FluidPressureSearchTaskSmallRangeRelativeResult(int cx, int cy, int cz) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
    }

    public FluidPressureSearchTaskSmallRangeRelativeResult(Vec3i centerPos){
        this(centerPos.getX(),centerPos.getY(),centerPos.getZ());
    }

    public FluidPressureSearchTaskSmallRangeRelativeResult(IVec3i centerPos){
        this(centerPos.getX(),centerPos.getY(),centerPos.getZ());
    }

    public void put(IVec3i pos){
        res.add(pos.toInt());
    }

    public Collection<BlockPos> toResultCollection(){
        ArrayList<BlockPos> list = new ArrayList<>(res.size());
        for(int p:res){
            list.add(getPosFromInt(p).toImmutable());
        }
        return list;
    }

    @Override
    public boolean hasNext() {
        return curIndex < res.size();
    }

    @Override
    public int size() {
        return res.size();
    }

    /**
     * @return 返回一个BlockPos.MutableBlockPos的结果位置
     */
    @Override
    @Nullable
    public BlockPos next() {
        if(!hasNext()) return null;
        return getPosFromInt(res.get(curIndex++));
    }

    protected BlockPos getPosFromInt(int posInt){
        final int x = Int10.toInt((posInt& IVec3i.X_INT_MASK)>> X_INT_OFFSET),
                y = Int10.toInt((posInt&IVec3i.Y_INT_MASK)>> Y_INT_OFFSET),
                z = Int10.toInt(posInt&IVec3i.Z_INT_MASK);
        return mutablePos.setPos(cx+x,cy+y,cz+z);
    }
}
