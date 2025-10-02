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

import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class RelativeBlockPosS extends Vec3s{
    public RelativeBlockPosS(short rx, short ry, short rz) {
        super(rx, ry, rz);
    }
    public RelativeBlockPosS(int x,int y,int z,int tx,int ty,int tz){
        this((short) (tx-x), (short) (ty-y), (short) (tz-z));
    }
    public RelativeBlockPosS(@Nonnull Vec3i origin, @Nonnull Vec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosS(@Nonnull IVec3i origin, @Nonnull IVec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosS(IVec3i pos){
        super(pos);
    }

    public static class Mutable extends RelativeBlockPosS{
        public static final ThreadLocal<Mutable> MUTABLE = ThreadLocal.withInitial(Mutable::new);
        public Mutable(){
            super((short) 0, (short) 0, (short) 0);
        }

        public Mutable(short ax, short ay, short az) {
            super(ax, ay, az);
        }

        public Mutable(int x, int y, int z, int tx, int ty, int tz) {
            super(x, y, z, tx, ty, tz);
        }
        public Mutable(Vec3i origin,Vec3i target) {
            super(origin,target);
        }
        public Mutable(IVec3i origin,IVec3i target) {
            super(origin,target);
        }

        public Mutable setPos(short x,short y,short z){
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Mutable setPos(int cx, int cy, int cz, int ax, int ay, int az){
            return this.setPos((short) (ax-cx),(short) (ay-cy),(short) (az-cz));
        }

        public Mutable setPos(@Nonnull IVec3i vec3i){
            return this.setPos((short) vec3i.getX(),(short) vec3i.getY(),(short) vec3i.getZ());
        }

        public Mutable setPos(@Nonnull IVec3i center, @Nonnull IVec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        public Mutable setPos(@Nonnull Vec3i center, @Nonnull Vec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        @Nonnull
        @Override
        public RelativeBlockPosS toImmutable(){
            return new RelativeBlockPosS(this);
        }
    }

    /**
     * 指定中心点的相对可变坐标
     */
    public static class CenteredMutable extends Mutable implements ICenteredMutableRelativeBlockPos{
        protected int cx,cy,cz; //中心点坐标

        /**
         * 创建一个中心点为(0,0,0)的相对坐标(~0,~0,~0)
         */
        public CenteredMutable(){
            super();
        }

        /**
         * 创建一个中心点为(0,0,0),为(~rx,~ry,~rz)的相对坐标
         * @param rx 相对中心点的X坐标
         * @param ry 相对中心点的Y坐标
         * @param rz 相对中心点的Z坐标
         */
        public CenteredMutable(short rx, short ry, short rz) {
            super(rx, ry, rz);
        }

        /**
         * 创建一个中心点为(x,y,z),目标点为(tx,ty,tz)的相对坐标(~(tx-x),~(ty-y),~(tz-z))
         * @param x 中心点X坐标
         * @param y 中心点Y坐标
         * @param z 中心点Z坐标
         * @param tx 目标点X坐标
         * @param ty 目标点Y坐标
         * @param tz 目标点Z坐标
         */
        public CenteredMutable(int x, int y, int z, int tx, int ty, int tz) {
            super(x, y, z, tx, ty, tz);
            setCenterPos(x,y,z);
        }

        /**
         * 创建一个相对原点为origin，绝对坐标为target的相对坐标
         * @param origin 原点坐标
         * @param target 绝对坐标
         */
        public CenteredMutable(Vec3i origin,Vec3i target){
            super(origin,target);
            setCenterPos(origin.getX(),origin.getY(),origin.getZ());
        }

        public CenteredMutable(IVec3i origin,IVec3i target){
            super(origin,target);
            setCenterPos(origin.getX(),origin.getY(),origin.getZ());
        }

        /**
         * 设置中心点坐标
         * @param x 中心点X坐标
         * @param y 中心点Y坐标
         * @param z 中心点Z坐标
         */
        @Override
        public CenteredMutable setCenterPos(int x,int y,int z){
            this.cx = x;
            this.cy = y;
            this.cz = z;
            return this;
        }

        /**
         * 设置表示的绝对坐标
         * @param x 绝对坐标X
         * @param y 绝对坐标Y
         * @param z 绝对坐标Z
         */
        public CenteredMutable setAbsolutePos(int x,int y,int z){
            this.setPos((short) (x-cx),(short) (y-cy),(short) (z-cz));
            return this;
        }
    }
}
