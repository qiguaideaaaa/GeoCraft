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

package moe.qingu.orbtellus.util.laminarifer;

import moe.qingu.orbtellus.api.laminarifer.ILaminarifer;
import moe.qingu.orbtellus.api.laminarifer.ImpetusPulse;
import moe.qingu.orbtellus.api.laminarifer.Laminarifers;
import moe.qingu.orbtellus.api.laminarifer.source.IFlowSource;
import moe.qingu.orbtellus.api.util.modifier.BlockFlagModifier;
import moe.qingu.orbtellus.api.util.modifier.BlockFlagModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author QGMoe
 */
@NotThreadSafe
public final class FillLaminariferRequest implements AutoCloseable{
    private static final int STATUS_NONE = 0;
    private static final int STATUS_REFUSED = -1;
    private static final int STATUS_ALLOWED = 1;
    private static final int STATUS_REQUESTED = 2;

    private boolean using = false;
    private World world;
    private BlockPos pos;
    private IBlockState state;
    private @Nullable EnumFacing side;

    private ILaminarifer laminarifer;

    private Fluid fluid;
    private long qb;
    private @Nullable NBTTagCompound nbt;

    private long pulse;
    private @Nullable IFlowSource fillSource;

    private long modifier = BlockFlagModifiers.KEEP;

    private int status = STATUS_NONE;

    public boolean isUsing(){
        return this.using;
    }

    @Nonnull
    public FillLaminariferRequest open(){
        this.using = true;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest at(final @Nonnull World world, final @Nonnull BlockPos pos, final @Nonnull IBlockState state){
        this.world = world;
        this.pos = pos;
        this.state = state;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest target(final @Nonnull ILaminarifer laminarifer){
        this.laminarifer = laminarifer;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest side(final @Nullable EnumFacing side){
        this.side = side;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest withSource(final @Nullable IFlowSource source){
        this.fillSource = source;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest withContent(final @Nonnull Fluid fluid,final long amountInQB){
        this.fluid = fluid;
        this.qb = amountInQB;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest withNBT(final @Nullable NBTTagCompound nbt){
        this.nbt = nbt;
        return this;
    }

    @Nonnull
    public FillLaminariferRequest withImpetusPulse(final float pressure,final float time){
        this.pulse = ImpetusPulse.of(pressure,time);
        return this;
    }

    @Nonnull
    public FillLaminariferRequest disableFlags(final int disabledFlags){
        this.modifier = BlockFlagModifier.disableFor(this.modifier,disabledFlags);
        return this;
    }

    @Nonnull
    public FillLaminariferRequest enableFlags(final int enabledFlags){
        this.modifier = BlockFlagModifier.enableFor(this.modifier,enabledFlags);
        return this;
    }

    public long fill(final boolean doOperate){
        switch (status){
            case STATUS_REQUESTED: throw new IllegalStateException();
            case STATUS_NONE:{
                if(laminarifer.canFill(world,pos,state,side,fluid,nbt,fillSource)) this.status = STATUS_ALLOWED; //继续ALLOWED的分支
                else{
                    this.status = STATUS_REFUSED;
                    return 0L;
                }
            } case STATUS_ALLOWED: {
                if(doOperate) this.status = STATUS_REQUESTED;
                return Laminarifers.addAmountInQB(laminarifer, world, pos, state, fluid, nbt, qb, doOperate, pulse, fillSource, modifier);
            }
            case STATUS_REFUSED:
            default:return 0L;
        }
    }

    @Override
    public void close(){
        this.world = null;
        this.pos = null;
        this.state = null;
        this.side = null;

        this.laminarifer = null;

        this.fluid = null;
        this.qb = 0L;
        this.nbt = null;

        this.pulse = 0L;
        this.fillSource = null;

        this.modifier = BlockFlagModifiers.KEEP;

        this.status = STATUS_NONE;
        this.using = false;
    }
}
