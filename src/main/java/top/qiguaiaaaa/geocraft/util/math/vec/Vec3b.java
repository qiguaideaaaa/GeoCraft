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

package top.qiguaiaaaa.geocraft.util.math.vec;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.util.math.Int10.toInt10;

/**
 * @author QiguaiAAAA
 */
public class Vec3b implements IVec3i{
    public static final Vec3b NULL_VEC = new Vec3b((byte) 0, (byte) 0, (byte) 0);
    protected byte x,y,z;
    public Vec3b(byte x,byte y,byte z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3b(Vec3b vec){
        this(vec.x, vec.y, vec.z);
    }

    public Vec3b(IVec3i vec){
        this((byte) vec.getX(), (byte) vec.getY(), (byte) vec.getZ());
    }

    public byte getBX() {
        return x;
    }

    public byte getBY() {
        return y;
    }

    public byte getBZ() {
        return z;
    }

    @Override
    public int toInt(){
        return toInt10(x)<<20 | toInt10(y)<<10 | toInt10(z);
    }

    @Override
    public int hashCode() {
        return (x<<16) | (y<<8) |z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3b) {
            Vec3b b = (Vec3b) obj;
            return b.getBX() == getBX() && b.getBY() == getBY() && b.getBZ() == getBZ();
        }
        return false;
    }

    //******
    //IVec3i
    //******

    @Override
    public int getX() {
        return getBX();
    }

    @Override
    public int getY() {
        return getBY();
    }

    @Override
    public int getZ() {
        return getBZ();
    }

    @Nonnull
    @Override
    public IVec3i toImmutable() {
        return this;
    }
}
