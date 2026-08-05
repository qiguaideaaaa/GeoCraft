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

package 清汩萌.天圆地方.原料;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import moe.qingu.orbtellus.api.block.BlockProperties;
import moe.qingu.orbtellus.geography.fluidphysics.finite.flow.FiniteFlowingVanilla;
import moe.qingu.orbtellus.geography.fluidphysics.finite.flow.FiniteFlowings;
import moe.qingu.orbtellus.handler.RegistryHandler;
import 清汩萌.天圆地方.方块.*;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.映射.映射;
import 清汩萌.造.映射.映射器;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.管理.映射局;
import 清汩萌.造.管理.空间构造局;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
@SuppressWarnings("DataFlowIssue")
public final class 方块原料 {

    public static final 空间构造器 _全构造器_;

    private static final Pair<MockBlockLiquid.MockBlockDynamicLiquid, MockBlockLiquid.MockBlockStaticLiquid> WATERS =
            MockBlockLiquid.create(b -> b.withID("water").withMaterial(Material.WATER).withMapColor(MapColor.WATER));

    private static final Pair<MockBlockLiquid.MockBlockDynamicLiquid, MockBlockLiquid.MockBlockStaticLiquid> LAVAS =
            MockBlockLiquid.create(b -> b.withID("lava").withMaterial(Material.LAVA));

    private static final BlockDynamicLiquid DYNAMIC_WATER = WATERS.getKey();
    private static final BlockStaticLiquid STATIC_WATER = WATERS.getRight();
    private static final BlockDynamicLiquid DYNAMIC_LAVA = LAVAS.getKey();
    private static final BlockStaticLiquid STATIC_LAVA = LAVAS.getRight();
    public static final 猹方块 _猹_ = new 猹方块();
    public static final 鲁迅方块 _鲁迅_ = new 鲁迅方块();
    public static final 闰土方块 _闰土_ = new 闰土方块();

    public static final class LayeredFluidHosts{
        public static 模拟载流方块 FLUID_HOST_COMMON = new 模拟载流方块();
    }

    @SuppressWarnings("unused")
    public static final class 常用 {
        public static final 映射器 _常用映射_;

        public static final IBlockState 〇 = Blocks.AIR.getDefaultState();

        public static final IBlockState 石 = Blocks.STONE.getDefaultState();
        public static final IBlockState 崗 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.GRANITE); //繁体岗，表示未打磨的花岗岩
        public static final IBlockState 岗 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.GRANITE_SMOOTH); //简体，表示已打磨，看起来简单了
        public static final IBlockState 閃 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.DIORITE); //繁体闪，表示未打磨的闪长岩
        public static final IBlockState 闪 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.DIORITE_SMOOTH); //简体，表示已打磨
        public static final IBlockState 峖 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.ANDESITE); //带有山字旁，表示未打磨的安山岩
        public static final IBlockState 安 = 石.withProperty(BlockStone.VARIANT,BlockStone.EnumType.ANDESITE_SMOOTH); //没有山字旁，简单了，表示已打磨的安山岩

        public static final IBlockState 基 = Blocks.BEDROCK.getDefaultState();

        public static final IBlockState 圆 = Blocks.COBBLESTONE.getDefaultState();
        public static final IBlockState 曜 = Blocks.OBSIDIAN.getDefaultState();

        static {
            _常用映射_ = new 映射器(new ResourceLocation("basic")).导入映射数据(常用.class);
        }
    }

    @SuppressWarnings("unused")
    public static final class 土壤 {
        public static final 映射器 _土壤映射_;

        /*
            Dirt 泥土
        */
        public static final IBlockState 土0 = Blocks.DIRT.getDefaultState().withProperty(BlockProperties.HUMIDITY,0); //后面的数字表示含水量
        public static final IBlockState 土1 = 土0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 土2 = 土0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 土3 = 土0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 土4 = 土0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 土 = 土0;

        public static final IBlockState 雪土0 = 土.withProperty(BlockDirt.SNOWY,true);
        public static final IBlockState 雪土1 = 雪土0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 雪土2 = 雪土0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 雪土3 = 雪土0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 雪土4 = 雪土0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 雪土 = 雪土0;

        /*
                Coarse Dirt 砂土（沙土）
         */
        public static final IBlockState 砂0 = 土.withProperty(BlockDirt.VARIANT,BlockDirt.DirtType.COARSE_DIRT);
        public static final IBlockState 砂1 = 砂0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 砂2 = 砂0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 砂3 = 砂0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 砂4 = 砂0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 砂 = 砂0;

        public static final IBlockState 雪砂0 = 砂0.withProperty(BlockDirt.SNOWY,true);
        public static final IBlockState 雪砂1 = 雪砂0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 雪砂2 = 雪砂0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 雪砂3 = 雪砂0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 雪砂4 = 雪砂0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 雪砂 = 雪砂0;

        /*
            Podzol 灰化土
         */
        /**
         * 灰化土，在寒冷地带砂土（也称沙土，这里简化为少）表层覆盖有有机物，由于气温低有机物难以分解，在表层形成灰色的有机层，即为草字头，该字读少。
         */
        public static final IBlockState 䒚0 = 土.withProperty(BlockDirt.VARIANT,BlockDirt.DirtType.PODZOL);
        public static final IBlockState 䒚1 = 䒚0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 䒚2 = 䒚0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 䒚3 = 䒚0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 䒚4 = 䒚0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 䒚 = 䒚0;

        public static final IBlockState 雪䒚0 = 䒚.withProperty(BlockDirt.SNOWY,true);
        public static final IBlockState 雪䒚1 = 雪䒚0.withProperty(BlockProperties.HUMIDITY,1);
        public static final IBlockState 雪䒚2 = 雪䒚0.withProperty(BlockProperties.HUMIDITY,2);
        public static final IBlockState 雪䒚3 = 雪䒚0.withProperty(BlockProperties.HUMIDITY,3);
        public static final IBlockState 雪䒚4 = 雪䒚0.withProperty(BlockProperties.HUMIDITY,4);

        @映射.别名
        public static final IBlockState 雪䒚 = 雪䒚0;

        static {
            _土壤映射_ = new 映射器(new ResourceLocation(天圆地方测试.MODID,"soil")).导入映射数据(土壤.class);
        }
    }

    @SuppressWarnings("unused")
    public static final class 原版流体 {
        public static final 映射器 _原版流体映射_;
        public static final 空间构造器 _流体物理构造器_;

        public static final IBlockState 丶 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7); // 主 （读音）
        public static final IBlockState 冫 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,6); // 冰
        public static final IBlockState 氵 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,5); // 水
        public static final IBlockState 灬 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,4); // 火
        public static final IBlockState 水 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,3);
        public static final IBlockState 沝 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,2); // 子
        public static final IBlockState 淼 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,1); // 汪淼的淼
        public static final IBlockState 㵘 = STATIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,0); // 打 水水水水

        public static final IBlockState 丶v = 丶.withProperty(BlockLiquid.LEVEL,15); // v 表示垂直流动，在 FINITE 模式下目前不应当出现，这是原版的一个奇怪的设计，包括模组也没有
        public static final IBlockState 冫v = 丶.withProperty(BlockLiquid.LEVEL,14);
        public static final IBlockState 氵v = 丶.withProperty(BlockLiquid.LEVEL,13);
        public static final IBlockState 灬v = 丶.withProperty(BlockLiquid.LEVEL,12);
        public static final IBlockState 水v = 丶.withProperty(BlockLiquid.LEVEL,11);
        public static final IBlockState 沝v = 丶.withProperty(BlockLiquid.LEVEL,10);
        public static final IBlockState 淼v = 丶.withProperty(BlockLiquid.LEVEL,9);
        public static final IBlockState 㵘v =丶.withProperty(BlockLiquid.LEVEL,8);

        public static final IBlockState 涸 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7); //几乎干涸
        public static final IBlockState 浅 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,6); //浅水
        public static final IBlockState 涓 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,5); //涓涓细流
        public static final IBlockState 盈 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,4); //充盈的水流
        public static final IBlockState 涨 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,3); //涨起来的水流
        public static final IBlockState 洪 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,2); //洪水
        public static final IBlockState 滔 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,1); //滔滔不绝
        public static final IBlockState 溢 = DYNAMIC_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,0); //快溢出来了

        public static final IBlockState 涸v = 涸.withProperty(BlockLiquid.LEVEL,15);
        public static final IBlockState 浅v = 涸.withProperty(BlockLiquid.LEVEL,14);
        public static final IBlockState 涓v = 涸.withProperty(BlockLiquid.LEVEL,13);
        public static final IBlockState 盈v = 涸.withProperty(BlockLiquid.LEVEL,12);
        public static final IBlockState 涨v = 涸.withProperty(BlockLiquid.LEVEL,11);
        public static final IBlockState 洪v = 涸.withProperty(BlockLiquid.LEVEL,10);
        public static final IBlockState 滔v = 涸.withProperty(BlockLiquid.LEVEL,9);
        public static final IBlockState 溢v = 涸.withProperty(BlockLiquid.LEVEL,8);

        public static final IBlockState 火 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,7);
        public static final IBlockState 炎 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,6);
        public static final IBlockState 焱 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,5);
        public static final IBlockState 燚 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,4);
        public static final IBlockState 日 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,3);
        public static final IBlockState 昍 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,2);
        public static final IBlockState 晿 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,1);
        public static final IBlockState 𣊭 = STATIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,0);

        public static final IBlockState 火v = 火.withProperty(BlockLiquid.LEVEL,15);
        public static final IBlockState 炎v = 火.withProperty(BlockLiquid.LEVEL,14);
        public static final IBlockState 焱v = 火.withProperty(BlockLiquid.LEVEL,13);
        public static final IBlockState 燚v = 火.withProperty(BlockLiquid.LEVEL,12);
        public static final IBlockState 日v = 火.withProperty(BlockLiquid.LEVEL,11);
        public static final IBlockState 昍v = 火.withProperty(BlockLiquid.LEVEL,10);
        public static final IBlockState 晿v = 火.withProperty(BlockLiquid.LEVEL,9);
        public static final IBlockState 𣊭v = 火.withProperty(BlockLiquid.LEVEL,8);

        public static final IBlockState 熙 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,7);
        public static final IBlockState 旭 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,6);
        public static final IBlockState 晁 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,5);
        public static final IBlockState 昇 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,4);
        public static final IBlockState 晅 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,3);
        public static final IBlockState 暑 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,2);
        public static final IBlockState 炽 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,1);
        public static final IBlockState 灼 = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,0);

        public static final IBlockState 熙v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,15);
        public static final IBlockState 旭v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,14);
        public static final IBlockState 晁v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,13);
        public static final IBlockState 昇v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,12);
        public static final IBlockState 晅v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,11);
        public static final IBlockState 暑v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,10);
        public static final IBlockState 炽v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,9);
        public static final IBlockState 灼v = DYNAMIC_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL,8);

        static {
            _原版流体映射_ = new 映射器(new ResourceLocation("liquid")).导入映射数据(原版流体.class);
            _流体物理构造器_ = new 空间构造器().添加映射(常用._常用映射_).添加映射(_原版流体映射_);
        }

        @Nonnull
        public static FiniteFlowingVanilla getFlowingByMaterial(final @Nonnull Material material){
            return material.isLiquid()?material == Material.WATER? FiniteFlowings.WATER_FLOW : FiniteFlowings.LAVA_FLOW :Assertions.fail("Unknown Liquid Type!");
        }
    }

    @SuppressWarnings("unused")
    public static final class 天圆地方之雪 {
        public static final 映射器 _天圆地方之雪映射_;

        public static final IBlockState 霁 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,1);
        public static final IBlockState 霰 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,2);
        public static final IBlockState 霙 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,3);
        public static final IBlockState 霏 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,4);
        public static final IBlockState 雱 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,5);
        public static final IBlockState 霈 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,6);
        public static final IBlockState 雹 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,7);
        public static final IBlockState 霃 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,8);

        public static final IBlockState 凉 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,1).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 冷 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,2).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 寒 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,3).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 冽 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,4).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 凝 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,5).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 凘 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,6).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 凌 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,7).withProperty(BlockProperties.MIXTURE,true);
        public static final IBlockState 冻 = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,8).withProperty(BlockProperties.MIXTURE,true);

        static {
            _天圆地方之雪映射_ = new 映射器(new ResourceLocation(天圆地方测试.MODID,"snow")).导入映射数据(天圆地方之雪.class);
        }
    }

    @SuppressWarnings("unused")
    public static final class 方块计划刻测试方块 {
        public static final 空间构造器 _方块计划刻测试构造器_;
        public static final 映射器 _方块计划刻测试映射_;

        public static final IBlockState 猹0 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,0);
        public static final IBlockState 猹1 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,1);
        public static final IBlockState 猹2 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,2);
        public static final IBlockState 猹3 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,3);
        public static final IBlockState 猹4 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,4);
        public static final IBlockState 猹5 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,5);
        public static final IBlockState 猹6 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,6);
        public static final IBlockState 猹7 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,7);
        public static final IBlockState 猹8 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,8);
        public static final IBlockState 猹9 = _猹_.getDefaultState().withProperty(猹方块._出现时间_,9);
        public static final IBlockState 猹a = _猹_.getDefaultState().withProperty(猹方块._出现时间_,10);
        public static final IBlockState 猹b = _猹_.getDefaultState().withProperty(猹方块._出现时间_,11);
        public static final IBlockState 猹c = _猹_.getDefaultState().withProperty(猹方块._出现时间_,12);
        public static final IBlockState 猹d = _猹_.getDefaultState().withProperty(猹方块._出现时间_,13);
        public static final IBlockState 猹e = _猹_.getDefaultState().withProperty(猹方块._出现时间_,14);
        public static final IBlockState 猹f = _猹_.getDefaultState().withProperty(猹方块._出现时间_,15);
        public static final @映射.别名 IBlockState 猹 = 猹0;

        public static final IBlockState 鲁x = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,0).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁o = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,1).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁λ = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,2).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁v = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,3).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁э = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,4).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁e = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,5).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁4 = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,6).withProperty(鲁迅方块._增减模式_,false);
        public static final IBlockState 鲁6 = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,7).withProperty(鲁迅方块._增减模式_,false);
        public static final @映射.别名 IBlockState 鲁 = 鲁6;
        public static final IBlockState 讯x = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,0).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯o = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,1).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯λ = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,2).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯v = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,3).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯э = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,4).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯e = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,5).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯4 = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,6).withProperty(鲁迅方块._增减模式_,true);
        public static final IBlockState 讯6 = _鲁迅_.getDefaultState().withProperty(鲁迅方块._方位模式_,7).withProperty(鲁迅方块._增减模式_,true);
        public static final @映射.别名 IBlockState 讯 = 讯6;

        public static final IBlockState 閏8 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,0);
        public static final IBlockState 閏7 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,1);
        public static final IBlockState 閏6 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,2);
        public static final IBlockState 閏5 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,3);
        public static final IBlockState 閏4 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,4);
        public static final IBlockState 閏3 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,5);
        public static final IBlockState 閏2 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,6);
        public static final IBlockState 閏1 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,7);
        public static final @映射.别名 IBlockState 閏 = 閏1;
        public static final IBlockState 閏0 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,8);
        public static final @映射.别名 IBlockState 闰0 = 閏0;
        public static final IBlockState 闰1 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,9);
        public static final @映射.别名 IBlockState 闰 = 闰1;
        public static final IBlockState 闰2 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,10);
        public static final IBlockState 闰3 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,11);
        public static final IBlockState 闰4 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,12);
        public static final IBlockState 闰5 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,13);
        public static final IBlockState 闰6 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,14);
        public static final IBlockState 闰7 = _闰土_.getDefaultState().withProperty(闰土方块._计划时间_,15);

        static {
            _方块计划刻测试映射_ = new 映射器(new ResourceLocation(天圆地方测试.MODID,"scheduler")).导入映射数据(方块计划刻测试方块.class);
            _方块计划刻测试构造器_ = new 空间构造器()
                    .添加映射(_方块计划刻测试映射_)
                    .添加映射(常用._常用映射_);
        }
    }

    static {
        RegistryHandler.registerVanillaBlockOverride(STATIC_WATER.setHardness(100.0F).setLightOpacity(3));
        RegistryHandler.registerVanillaBlockOverride(STATIC_LAVA.setHardness(100.0F).setLightLevel(1.0F).setLightOpacity(3));
        RegistryHandler.registerVanillaBlockOverride(DYNAMIC_WATER.setHardness(100.0F).setLightOpacity(3));
        RegistryHandler.registerVanillaBlockOverride(DYNAMIC_LAVA.setHardness(100.0F).setLightLevel(1.0F).setLightOpacity(3));
        Block.registerBlocks();
        Block.REGISTRY.register(256,_猹_.getRegistryName(), _猹_);
        Block.REGISTRY.register(257,_鲁迅_.getRegistryName(),_鲁迅_);
        Block.REGISTRY.register(258,_闰土_.getRegistryName(),_闰土_);
        _全构造器_ = new 空间构造器()
                .添加映射(常用._常用映射_)
                .添加映射(土壤._土壤映射_)
                .添加映射(原版流体._原版流体映射_)
                .添加映射(天圆地方之雪._天圆地方之雪映射_)
                .添加映射(方块计划刻测试方块._方块计划刻测试映射_);
        映射局.登记(常用._常用映射_);
        映射局.登记(土壤._土壤映射_);
        映射局.登记(原版流体._原版流体映射_);
        映射局.登记(天圆地方之雪._天圆地方之雪映射_);
        映射局.登记(方块计划刻测试方块._方块计划刻测试映射_);
        空间构造局.登记(new ResourceLocation(天圆地方测试.MODID,"all"), _全构造器_);
        空间构造局.登记(new ResourceLocation("liquids"), 原版流体._流体物理构造器_);
        空间构造局.登记(new ResourceLocation(天圆地方测试.MODID,"scheduler"), 方块计划刻测试方块._方块计划刻测试构造器_);
    }
}
