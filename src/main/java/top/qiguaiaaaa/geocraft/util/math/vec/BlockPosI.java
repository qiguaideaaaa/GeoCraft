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

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class BlockPosI extends BlockPos implements IVec3i {
    public static final BlockPosI ORIGIN = new BlockPosI(0,0,0);
    public BlockPosI(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    public BlockPosI(double xIn, double yIn, double zIn) {
        super(xIn, yIn, zIn);
    }

    public BlockPosI(@Nonnull Entity source){
        this(source.posX,source.posY,source.posZ);
    }

    public BlockPosI(@Nonnull Vec3d vec){
        this(vec.x, vec.y,vec.z);
    }

    public BlockPosI(@Nonnull Vec3i vec){
        this(vec.getX(),vec.getY(),vec.getZ());
    }

    public BlockPosI(@Nonnull IVec3i vec){
        this(vec.getX(),vec.getY(),vec.getZ());
    }

    @Nonnull
    @Override
    public BlockPosI add(double x, double y, double z) {
        return add((int) x,(int) y,(int) z);
    }

    @Nonnull
    @Override
    public BlockPosI add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this: new BlockPosI(getX()+x,getY()+y,getZ()+z);
    }

    @Nonnull
    @Override
    public BlockPosI add(@Nonnull Vec3i vec) {
        return add(vec.getX(),vec.getY(),vec.getZ());
    }

    @Nonnull
    @Override
    public BlockPosI subtract(@Nonnull Vec3i vec) {
        return this.add(-vec.getX(),-vec.getY(),-vec.getZ());
    }

    @Nonnull
    public BlockPosI subtract(@Nonnull IVec3i vec) {
        return this.add(-vec.getX(),-vec.getY(),-vec.getZ());
    }

    @Nonnull
    @Override
    public BlockPosI up() {
        return up(1);
    }

    @Nonnull
    @Override
    public BlockPosI up(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    @Nonnull
    @Override
    public BlockPosI down() {
        return down(1);
    }

    @Nonnull
    @Override
    public BlockPosI down(int n) {
        return this.offset(EnumFacing.DOWN,n);
    }

    @Nonnull
    @Override
    public BlockPosI north() {
        return north(1);
    }

    @Nonnull
    @Override
    public BlockPosI north(int n) {
        return this.offset(EnumFacing.NORTH,n);
    }

    @Nonnull
    @Override
    public BlockPosI south() {
        return south(1);
    }

    @Nonnull
    @Override
    public BlockPosI south(int n) {
        return this.offset(EnumFacing.SOUTH,n);
    }

    @Nonnull
    @Override
    public BlockPosI west() {
        return west(1);
    }

    @Nonnull
    @Override
    public BlockPosI west(int n) {
        return this.offset(EnumFacing.WEST,n);
    }

    @Nonnull
    @Override
    public BlockPosI east() {
        return east(1);
    }

    @Nonnull
    @Override
    public BlockPosI east(int n) {
        return this.offset(EnumFacing.EAST,n);
    }

    @Nonnull
    @Override
    public BlockPosI offset(@Nonnull EnumFacing facing) {
        return offset(facing,1);
    }

    @Nonnull
    @Override
    public BlockPosI offset(@Nonnull EnumFacing facing, int n) {
        return n == 0?this:
                add(facing.getXOffset()*n,facing.getYOffset()*n,facing.getZOffset()*n);
    }

    @Nonnull
    @Override
    public BlockPosI rotate(@Nonnull Rotation rotationIn) {
        switch (rotationIn) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new BlockPosI(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180:
                return new BlockPosI(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPosI(this.getZ(), this.getY(), -this.getX());
        }
    }

    @Nonnull
    @Override
    public BlockPosI crossProduct(@Nonnull Vec3i vec) {
        return new BlockPosI(this.getY() * vec.getZ() - this.getZ() * vec.getY(),
                this.getZ() * vec.getX() - this.getX() * vec.getZ(),
                this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    @Nonnull
    public BlockPosI crossProduct(@Nonnull IVec3i vec) {
        return new BlockPosI(this.getY() * vec.getZ() - this.getZ() * vec.getY(),
                this.getZ() * vec.getX() - this.getX() * vec.getZ(),
                this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    @Nonnull
    @Override
    public BlockPosI toImmutable() {
        return this;
    }

    @Nonnull
    public BlockPosI add(@Nonnull IVec3i vec) {
        return add(vec.getX(),vec.getY(),vec.getZ());
    }

    @Nonnull
    @Override
    public IVec3i asImmutable() {
        return this;
    }

    public static class Mutable extends BlockPosI{
        protected int x,y,z;

        public Mutable() {
            this(0,0,0);
        }

        public Mutable(int xIn, int yIn, int zIn) {
            super(xIn, yIn, zIn);
            setPos(xIn, yIn, zIn);
        }

        public Mutable(double xIn, double yIn, double zIn) {
            super(xIn, yIn, zIn);
            setPos(xIn, yIn, zIn);
        }

        public Mutable(@Nonnull Entity source) {
            super(source);
            setPos(source);
        }

        public Mutable(@Nonnull Vec3d vec) {
            super(vec);
            setPos(vec);
        }

        public Mutable(@Nonnull Vec3i vec) {
            super(vec);
            setPos(vec);
        }

        public Mutable(@Nonnull IVec3i vec) {
            super(vec);
            setPos(vec);
        }

        @Nonnull
        public Mutable addM(double x, double y, double z) {
            return setPos(this.getX() + x,this.getY() +y,this.getZ() +z);
        }

        @Nonnull
        public Mutable addM(int x, int y, int z) {
            return setPos(this.getX() + x,this.getY() +y,this.getZ() +z);
        }

        @Nonnull
        public Mutable addM(@Nonnull Vec3i vec) {
            return addM(vec.getX(),vec.getY(),vec.getZ());
        }

        @Nonnull
        public Mutable addM(@Nonnull IVec3i vec) {
            return addM(vec.getX(),vec.getY(),vec.getZ());
        }

        @Nonnull
        public Mutable subtractM(@Nonnull Vec3i vec) {
            return this.addM(-vec.getX(),-vec.getY(),-vec.getZ());
        }

        @Nonnull
        @Override
        public Mutable subtract(@Nonnull IVec3i vec) {
            return this.addM(-vec.getX(),-vec.getY(),-vec.getZ());
        }

        @Nonnull
        @Override
        public BlockPosI toImmutable() {
            return new BlockPosI((Vec3i) this);
        }

        @Nonnull
        public Mutable upM() {
            return upM(1);
        }

        @Nonnull
        public Mutable upM(int n) {
            return this.offsetM(EnumFacing.UP,n);
        }

        @Nonnull
        public Mutable downM() {
            return downM(1);
        }

        @Nonnull
        public Mutable downM(int n) {
            return offsetM(EnumFacing.DOWN,n);
        }

        @Nonnull
        public Mutable northM() {
            return this.northM(1);
        }

        @Nonnull
        public Mutable northM(int n) {
            return offsetM(EnumFacing.NORTH,n);
        }

        @Nonnull
        public Mutable southM() {
            return southM(1);
        }

        @Nonnull
        public Mutable southM(int n) {
            return offsetM(EnumFacing.SOUTH,n);
        }

        @Nonnull
        public Mutable westM() {
            return westM(1);
        }

        @Nonnull
        public Mutable westM(int n) {
            return offsetM(EnumFacing.WEST,n);
        }

        @Nonnull
        public Mutable eastM() {
            return eastM(1);
        }

        @Nonnull
        public Mutable eastM(int n) {
            return offsetM(EnumFacing.EAST,n);
        }

        @Nonnull
        public Mutable offsetM(@Nonnull EnumFacing facing) {
            return offsetM(facing,1);
        }

        @Nonnull
        public Mutable offsetM(@Nonnull EnumFacing facing, int n) {
            return n == 0? this:
                    addM(facing.getXOffset()*n,facing.getYOffset()*n,facing.getZOffset()*n);
        }

        @Nonnull
        public Mutable rotateM(@Nonnull Rotation rotationIn) {
            switch (rotationIn) {
                case NONE:
                default:
                    return this;
                case CLOCKWISE_90:
                    return setPos(-this.getZ(), this.getY(), this.getX());
                case CLOCKWISE_180:
                    return setPos(-this.getX(), this.getY(), -this.getZ());
                case COUNTERCLOCKWISE_90:
                    return setPos(this.getZ(), this.getY(), -this.getX());
            }
        }

        @Nonnull
        public Mutable crossProductM(@Nonnull Vec3i vec) {
            return setPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(),
                    this.getZ() * vec.getX() - this.getX() * vec.getZ(),
                    this.getX() * vec.getY() - this.getY() * vec.getX());
        }

        @Nonnull
        public Mutable crossProductM(@Nonnull IVec3i vec) {
            return setPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(),
                    this.getZ() * vec.getX() - this.getX() * vec.getZ(),
                    this.getX() * vec.getY() - this.getY() * vec.getX());
        }

        public Mutable setPos(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Mutable setPos(double x, double y, double z){
            this.x = (int) x;
            this.y = (int) y;
            this.z = (int) z;
            return this;
        }

        public Mutable setPos(@Nonnull IVec3i vec3i){
            return this.setPos(vec3i.getX(),vec3i.getY(), vec3i.getZ());
        }

        public Mutable setPos(@Nonnull Vec3d vec3d){
            return this.setPos(vec3d.x,vec3d.y,vec3d.z);
        }

        public Mutable setPos(@Nonnull Vec3i vec3i){
            return this.setPos(vec3i.getX(),vec3i.getY(), vec3i.getZ());
        }

        public Mutable setPos(@Nonnull Entity entity){
            return this.setPos(entity.posX,entity.posY,entity.posZ);
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }

        @Nonnull
        @Override
        public IVec3i asImmutable() {
            return new BlockPosI((IVec3i) this);
        }
    }
}
