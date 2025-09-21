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

/**
 * @author QiguaiAAAA
 */
public interface ICenteredMutableRelativeBlockPos extends IVec3i{
    /**
     * 设置中心点坐标
     * @param x 中心点X坐标
     * @param y 中心点Y坐标
     * @param z 中心点Z坐标
     */
    ICenteredMutableRelativeBlockPos setCenterPos(int x, int y, int z);

    /**
     * 设置中心点坐标
     * @param pos 中心点坐标
     */
    default ICenteredMutableRelativeBlockPos setCenterPos(Vec3i pos){
        return this.setCenterPos(pos.getX(),pos.getY(),pos.getZ());
    }

    default ICenteredMutableRelativeBlockPos setCenterPos(IVec3i pos){
        return this.setCenterPos(pos.getX(),pos.getY(),pos.getZ());
    }

    /**
     * 设置表示的绝对坐标
     * @param x 绝对坐标X
     * @param y 绝对坐标Y
     * @param z 绝对坐标Z
     */
    ICenteredMutableRelativeBlockPos setAbsolutePos(int x, int y, int z);

    default ICenteredMutableRelativeBlockPos setAbsolutePos(Vec3i pos){
        return this.setAbsolutePos(pos.getX(),pos.getY(),pos.getZ());
    }

    default ICenteredMutableRelativeBlockPos setAbsolutePos(IVec3i pos){
        return this.setAbsolutePos(pos.getX(),pos.getY(),pos.getZ());
    }
}
