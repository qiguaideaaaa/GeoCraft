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

package moe.qingu.orbtellus.command.atmosphere;

import moe.qingu.nickel.NickelAPI;
import moe.qingu.nickel.command.exception.NickelRuntimeException;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.nbt.path.NBTPath;
import moe.qingu.nickel.nbt.path.node.NBTPathCompound;
import moe.qingu.nickel.network.PackageNBTInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.apache.logging.log4j.Logger;
import moe.qingu.orbtellus.OrbTellusCraft;
import moe.qingu.orbtellus.api.OTCProperties;
import moe.qingu.orbtellus.api.atmosphere.Atmosphere;
import moe.qingu.orbtellus.api.atmosphere.AtmosphereSystemManager;
import moe.qingu.orbtellus.api.atmosphere.accessor.IAtmosphereAccessor;
import moe.qingu.orbtellus.api.atmosphere.layer.AtmosphereLayer;
import moe.qingu.orbtellus.api.atmosphere.layer.Layer;
import moe.qingu.orbtellus.api.atmosphere.storage.AtmosphereData;
import moe.qingu.orbtellus.api.atmosphere.system.IAtmosphereSystem;
import moe.qingu.orbtellus.api.atmosphere.tracker.InformationLoggingTracker;
import moe.qingu.nickel.command.builder.CommandBuilder;
import moe.qingu.nickel.command.builder.INodeBuilder;
import moe.qingu.nickel.command.builder.execute.CommandExecutor;
import moe.qingu.nickel.command.builder.execute.RelayExecuteNodeBuilder;
import moe.qingu.nickel.command.context.ExecuteContext;
import moe.qingu.nickel.command.node.ISmartNode;
import moe.qingu.nickel.command.node.parameter.minecraft.DimensionNode;
import moe.qingu.orbtellus.api.configs.value.minecraft.ConfigurableBlockState;
import moe.qingu.orbtellus.api.property.FluidProperty;
import moe.qingu.orbtellus.api.property.GeographyProperty;
import moe.qingu.orbtellus.api.property.IGeographyProperty;
import moe.qingu.orbtellus.api.setting.GeoBlockSetting;
import moe.qingu.orbtellus.api.state.FluidState;
import moe.qingu.orbtellus.api.state.GeographyState;
import moe.qingu.orbtellus.api.state.TemperatureState;
import moe.qingu.orbtellus.api.util.AtmosphereUtil;
import moe.qingu.orbtellus.api.util.io.FileLogger;
import moe.qingu.orbtellus.api.util.math.Altitude;
import moe.qingu.orbtellus.geography.atmosphere.SurfaceAtmosphere;
import moe.qingu.orbtellus.geography.atmosphere.tracker.FluidTracker;
import moe.qingu.orbtellus.geography.atmosphere.tracker.TemperatureTracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static moe.qingu.nickel.command.Nodes.*;
import static moe.qingu.nickel.text.Texts.translation;
import static moe.qingu.orbtellus.command.atmosphere.AtmosphereCommandContext.*;
import static moe.qingu.orbtellus.command.GeoArguments.*;

public final class CommandAtmosphere {
    public static final String ATMOSPHERE_COMMAND_NAME = "atmosphere";
    public static final String ATM_PERMISSION_NODE = "geocraft.command.atmosphere";
    public static final List<String> ALIASES = new ArrayList<>(Collections.singleton("大气"));

    @Nonnull
    public static ICommand create(@Nonnull final MinecraftServer server){
        return new CommandBuilder(ATMOSPHERE_COMMAND_NAME)
                .require(2)
                .require(ATM_PERMISSION_NODE).allow(DefaultPermissionLevel.OP).register()
                .addAlias(server.isSinglePlayer() && FMLCommonHandler.instance().getSide() == Side.CLIENT? "zh_cn".equals(
                        Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode()
                )?ALIASES:Collections.emptyList():Collections.emptyList())
                .smart()
                .append(buildSetCommand()).done()
                .append(buildStopCommand()).done()
                .append(buildResetCommand()).done()
                .append(buildAddCommand()).done()
                .append(buildQueryCommand()).done()
                .append(buildUtilCommand()).done()
                .append(buildDataCommand()).done()
                .append(buildTrackCommand()).done()
                .execute(buildDefaultExecutor()).done()
                .usage("geocraft.command.atmosphere.usage")
                .build();
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildSetCommand(){
        return literal("set")
                .require(ATM_PERMISSION_NODE +".set").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方$property().suggest(Stream.concat(getPropertyList().stream(), AtmosphereSets.SetConsumer.keySet().stream()))
                        .then(¥天圆地方$value().then(¥天圆地方_pos().then(process(CommandAtmosphere::set)))
        ));
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildStopCommand(){
        return literal("stop")
                .require(ATM_PERMISSION_NODE +".stop").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方_world().then(execute(ctx -> {
                            final World world = ctx.get(WORLD, DimensionNode.class);
                            final IAtmosphereSystem system = AtmosphereSystemManager.getAtmosphereSystem(world);
                            if(system == null) throw new CommandException("geocraft.command.atmosphere_system.null");
                            system.setStop(!system.isStopped());
                            if(system.isStopped()) ctx.notifyCommandListener("geocraft.command.atmosphere.stop.stopped",world.provider.getDimension());
                            else ctx.notifyCommandListener("geocraft.command.atmosphere.stop.running",world.provider.getDimension());
                            ctx.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,system.isStopped()?-1:1);
                        })
                )
        );
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildResetCommand(){
        return literal("reset")
                .require(ATM_PERMISSION_NODE +".reset").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方$property().allow("temp").then(
                        ¥天圆地方_pos().then(process(context -> {
                                    final World world = context.getWorld();
                                    final Atmosphere atmosphere = context.get(ATMOSPHERE);
                                    final BlockPos pos = context.getBlockPos(POS);
                                    final int x = pos.getX();
                                    final int z = pos.getZ();
                                    final String property = context.get(PROPERTY);

                                    if("temp".equalsIgnoreCase(property)){
                                        if(!world.isAreaLoaded(pos,1)){
                                            throw new CommandException("geocraft.command.chunk_error.unloaded",x,z);
                                        }
                                        final Chunk chunk = world.getChunk(pos);
                                        if(atmosphere instanceof SurfaceAtmosphere){
                                            ((SurfaceAtmosphere)atmosphere).重置温度(chunk);
                                        }else throw new CommandException("geocraft.command.atmosphere.unknown");
                                        context.notifyCommandListener("geocraft.command.atmosphere.reset.temp",x,z, atmosphere.getAtmosphereTemperature(pos));
                                    }
                                })
                        )
                )
        );
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildAddCommand(){
        return literal("add")
                .require(ATM_PERMISSION_NODE +".add").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方$property().suggest(
                        Stream.concat(getPropertyList().stream(), AtmosphereAdds.AddConsumer.keySet().stream())
                ).then(¥天圆地方$value().then(¥天圆地方_pos().then(process(CommandAtmosphere::add)))));
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildQueryCommand(){
        return literal("query")
                .require(ATM_PERMISSION_NODE +".query").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方$property()
                        .suggest(Stream.concat(getPropertyList().stream(), AtmosphereQueries.QueryConsumer.keySet().stream()))
                        .then(¥天圆地方_pos().then(¥天圆地方_multiply().then(process(CommandAtmosphere::query)))
        ));
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildUtilCommand(){
        return literal("util")
                .require(ATM_PERMISSION_NODE +".util").allow(DefaultPermissionLevel.OP).register()
                .smart()
                .literal("block_info")
                .then($blockState("blockToQuery")
                        .translate("geocraft.command.atmosphere.arg.block_to_query")
                        .asOptional()
                        .defaultAs(((node, context) -> context.getWorld().getBlockState(context.getSender().getPosition().down())))
                        .then($token("blockProp")
                                .asOptional()
                                .allow("reflectivity","heat_capacity","*")
                                .translate("geocraft.command.atmosphere.arg.blockProp")
                                .comment("geocraft.command.atmosphere.comment.blockProp")
                                .defaultAs("*")
                                .then(¥天圆地方_multiply().then(execute(ctx ->{
                                    final IBlockState state = ctx.get("blockToQuery");
                                    final double multiply = ctx.get(MULTIPLY);
                                    final ConfigurableBlockState cState = new ConfigurableBlockState(state);
                                    final int heatCapacity = GeoBlockSetting.getBlockHeatCapacity(state);
                                    final double reflectivity= GeoBlockSetting.getBlockReflectivity(state);
                                    ctx.notifyCommandListener("geocraft.command.atmosphere.util.block_info",cState,heatCapacity,reflectivity);
                                    final String prop = ctx.get("blockProp");
                                    if("reflectivity".equals(prop)) ctx.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) (reflectivity*multiply));
                                    else if("heat_capacity".equals(prop)) ctx.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(heatCapacity*multiply));
                                }))))
                ).done()
                .append($token("util_name")
                        .allow("sun","property","storage")
                        .translate("geocraft.command.atmosphere.arg.util_name")
                        .decorate((arg,context)->{
                            context.put(POS,context.getSender().getPosition());
                            return arg;
                        }).then(process(CommandAtmosphere::util))
                ).done()
                .done();
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildDataCommand(){
        return literal("data")
                .require(ATM_PERMISSION_NODE +".data").allow(DefaultPermissionLevel.OP).register()
                .then(literals()
                        .when("query")
                        .then($NBTPath("nbt_path")
                                .asOptional()
                                .defaultAs(new NBTPath().append(new NBTPathCompound(NBTMatcher.toMatcher(new NBTTagCompound()))))
                                .translate("geocraft.command.atmosphere.arg.nbt_path")
                                .suggestRaw("{}")
                                .then(¥天圆地方_pos().then(¥天圆地方_multiply().then(process(CommandAtmosphere::queryData)))))
                        .when("modify")
                        .then($NBTPath("nbt_path")
                                .translate("geocraft.command.atmosphere.arg.nbt_path")
                                .then($NBTTag("nbt_tag")
                                        .translate("geocraft.command.atmosphere.arg.nbt_tag")
                                        .then(¥天圆地方_pos().then(process(CommandAtmosphere::modifyData))))));
    }

    @Nonnull
    public static INodeBuilder<? extends ISmartNode> buildTrackCommand(){
        return literal("track")
                .requirePlayer(true)
                .require(4)
                .require(ATM_PERMISSION_NODE +".track").allow(DefaultPermissionLevel.OP).register()
                .then(¥天圆地方$property()
                        .suggest(
                                Stream.concat(getPropertyList().stream(), Stream.of("temp","water","steam"))
                        ).then($int("time")
                                .min(1)
                                .suggest(1)
                                .translate("geocraft.command.atmosphere.arg.time")
                                .comment("geocraft.command.atmosphere.comment.time")
                                .then($string("file_name")
                                        .translate("geocraft.command.atmosphere.arg.file_name")
                                        .comment("geocraft.command.atmosphere.comment.file_name")
                                        .pattern("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$") //过滤正确的文件名
                                        .suggest(() -> Collections.singletonList("track_"+new Date().getTime()+".csv"))
                                        .then(¥天圆地方_pos().then(process(CommandAtmosphere::track)))
                        )
                )
        );
    }

    @Nonnull
    public static CommandExecutor buildDefaultExecutor(){
        return context-> {
            final World world = context.getWorld();
            final BlockPos pos = context.getPosition();
            final IAtmosphereAccessor accessor = getAtmosphereAccessor(world,pos);
            final Atmosphere atmosphere = getAtmosphere(accessor);
            final ITextComponent title = new TextComponentTranslation("geocraft.command.atmosphere.query.basic",pos.getX(), Altitude.get物理海拔(pos.getY()),pos.getZ());
            title.getStyle().setColor(TextFormatting.AQUA);
            context.getSender().sendMessage(title);
            context.notifyCommandListener("geocraft.command.atmosphere.query.basic.1",
                    accessor.getPressure(),
                    accessor.getWaterPressure(),
                    accessor.getTemperature());
            context.notifyCommandListener("geocraft.command.atmosphere.query.basic.2",accessor.getWind());
            for(Layer layer = atmosphere.getBottomLayer(); layer != null; layer = layer.getUpperLayer()){
                context.notifyCommandListener("geocraft.command.atmosphere.query.basic.3",
                        layer.getTagName(),layer.getBeginY(),layer.getBeginY()+layer.getDepth());
                context.notifyCommandListener("geocraft.command.atmosphere.query.basic.4",layer.getTemperature());
                final FluidState steam = (layer instanceof AtmosphereLayer)?((AtmosphereLayer)layer).getSteam():null;
                final FluidState water = layer.getWater();
                context.notifyCommandListener("geocraft.command.atmosphere.query.basic.5",
                        steam==null?"NULL":steam,water==null?"NULL":water);
            }
        };
    }

    @Nonnull
    public static RelayExecuteNodeBuilder process(@Nonnull final CommandExecutor func){
        return relay(CommandAtmosphere::processAtmosphereInfo)
                .keepArguments(false)
                .then(execute(func))
                .after(CommandAtmosphere::afterProcessAtmosphereInfo);
    }

    static void processAtmosphereInfo(@Nonnull final ExecuteContext context) throws CommandException {
        final World world = context.getWorld();
        final BlockPos pos = context.getBlockPos(POS);
        final IAtmosphereAccessor accessor = getAtmosphereAccessor(world,pos);
        final Atmosphere atmosphere = getAtmosphere(accessor);
        final Layer layer = getAtmosphereLayer(atmosphere,pos.getY());
        context.put(ACCESSOR,accessor);
        context.put(ATMOSPHERE,atmosphere);
        context.put(LAYER,layer);
        context.notifyCommandListener("geocraft.command.atmosphere.query.layer_inf",layer.getTagName(),
                layer.getBeginY(),layer.getBeginY()+layer.getDepth());
    }

    static void afterProcessAtmosphereInfo(@Nonnull final ExecuteContext context) {
        final @Nullable IAtmosphereAccessor accessor = context.remove(ACCESSOR);
        if(accessor != null) accessor.close();
        context.remove(ATMOSPHERE);
        context.remove(LAYER);
    }

    static void set(@Nonnull final ExecuteContext context) throws CommandException{
        final double value = context.getDouble(VALUE);
        final String name = context.get(PROPERTY);
        final BiConsumer<AtmosphereCommandContext,Double> consumer = AtmosphereSets.SetConsumer.get(name.toLowerCase(Locale.ROOT));
        if(consumer != null){
            final AtmosphereCommandContext atmosphereCommandContext = new AtmosphereCommandContext(context);
            consumer.accept(atmosphereCommandContext,value);
            if(atmosphereCommandContext.exception != null) throw atmosphereCommandContext.exception;
            return;
        }

        final BlockPos pos = context.getBlockPos(POS);
        final Layer layer = context.get(LAYER);
        final GeographyState state = getState(getProperty(new ResourceLocation(name)),layer);
        if(state instanceof FluidState){
            final FluidState gas = (FluidState) state;
            gas.setAmount((int) value);
            context.notifyCommandListener("geocraft.command.atmosphere.set.gas",pos.getX(),pos.getY(),pos.getZ(),state.getProperty().getRegistryName(),gas.getFluidAmount());
            return;
        }
        if(state instanceof TemperatureState){
            final TemperatureState temperature = (TemperatureState) state;
            temperature.set((float) value);
            context.notifyCommandListener("geocraft.command.atmosphere.set.temp2",pos.getX(),pos.getY(),pos.getZ(),state.getProperty().getRegistryName(),temperature.get());
            return;
        }
        throw new CommandException("geocraft.command.atmosphere.property.unknown");
    }

    static void add(@Nonnull final ExecuteContext context) throws CommandException {
        final double value = context.getDouble(VALUE);
        final String name = context.get(PROPERTY);
        final BiConsumer<AtmosphereCommandContext,Double> consumer = AtmosphereAdds.AddConsumer.get(name.toLowerCase(Locale.ROOT));
        if(consumer != null){
            final AtmosphereCommandContext atmosphereCommandContext = new AtmosphereCommandContext(context);
            consumer.accept(atmosphereCommandContext,value);
            if(atmosphereCommandContext.exception != null) throw atmosphereCommandContext.exception;
            return;
        }

        final BlockPos pos = context.getBlockPos(POS);
        final int x = pos.getX();
        final int z = pos.getZ();
        final Layer layer = context.get(LAYER);
        final GeographyState state = getState(getProperty(new ResourceLocation(name)),layer);
        if(state instanceof FluidState){
            FluidState gas = (FluidState) state;
            gas.fill((int) value,true);
            context.notifyCommandListener("geocraft.command.atmosphere.add.gas",x,pos.getY(),z,state.getProperty().getRegistryName(),gas.getFluidAmount());
            return;
        }
        if(state instanceof TemperatureState){
            TemperatureState temperature = (TemperatureState) state;
            temperature.add(value);
            context.notifyCommandListener("geocraft.command.atmosphere.add.temp2",x,pos.getY(),z,state.getProperty().getRegistryName(),temperature.get());
            return;
        }
        throw new CommandException("geocraft.command.atmosphere.property.unknown");
    }

    static void query(@Nonnull final ExecuteContext context) throws CommandException{
        final String name = context.get(PROPERTY);
        final Consumer<AtmosphereCommandContext> consumer = AtmosphereQueries.QueryConsumer.get(name.toLowerCase(Locale.ROOT));
        if(consumer != null){
            final AtmosphereCommandContext atmosphereCommandContext = new AtmosphereCommandContext(context);
            consumer.accept(atmosphereCommandContext);
            if(atmosphereCommandContext.exception != null) throw atmosphereCommandContext.exception;
            return;
        }

        final BlockPos pos = context.getBlockPos(POS);
        final int x = pos.getX();
        final int z = pos.getZ();
        final Layer layer = context.get(LAYER);
        final GeographyState state = getState(getProperty(new ResourceLocation(name)),layer);
        if(state instanceof FluidState){
            final FluidState fluid = (FluidState) state;
            context.notifyCommandListener("geocraft.command.atmosphere.query.fluid",x,pos.getY(),z,state.getProperty().getRegistryName(),fluid.getFluidAmount());
            context.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,fluid.getFluidAmount());
            return;
        }else if(state instanceof TemperatureState){
            TemperatureState temperature = (TemperatureState) state;
            context.notifyCommandListener("geocraft.command.atmosphere.query.temperature",x,pos.getY(),z,state.getProperty().getRegistryName(),temperature.get());
            context.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,temperature.getInt());
            return;
        }
        throw new CommandException("geocraft.command.atmosphere.property.unknown");
    }

    static void util(@Nonnull final ExecuteContext context) {
        final World world = context.getWorld();
        final WorldInfo info = world.getWorldInfo();
        final String util = context.get("util_name");
        final IAtmosphereAccessor accessor = context.get(ACCESSOR);

        if("sun".equalsIgnoreCase(util)){
            context.notifyCommandListener("geocraft.command.atmosphere.util.sun");
            context.notifyCommandListener("geocraft.command.atmosphere.util.sun.1",
                    AtmosphereUtil.getSunHeight(info).getDegree());
            context.notifyCommandListener("geocraft.command.atmosphere.util.sun.2",
                    AtmosphereUtil.getSunEnergyPerChunk(info));
            return;
        }
        if("property".equalsIgnoreCase(util)){
            final IForgeRegistry<IGeographyProperty> registry = GeographyProperty.MANAGER.getProperties();
            for(IGeographyProperty property:registry){
                context.notifyCommandListener("geocraft.command.atmosphere.util.property",property.getRegistryName());
            }
            return;
        }
        if("storage".equalsIgnoreCase(util)){
            final @Nonnull Collection<AtmosphereData> data = accessor.getDataProvider().getLoadedAtmosphereDataCollection();
            context.notifyCommandListener("geocraft.command.atmosphere.util.storage",data.size());
        }
    }

    static void queryData(@Nonnull final ExecuteContext context) {
        final IAtmosphereAccessor accessor = context.get(ACCESSOR);
        final AtmosphereData data = accessor.getAtmosphereDataHere();
        assert data != null;
        final NBTPath path = context.get("nbt_path");
        final double multiply = context.getDouble(MULTIPLY);
        final List<NBTBase> nbt = path.resolve(data.getSaveCompound());
        final ICommandSender sender = context.getSender();
        if(nbt.size() == 1){
            final NBTBase tag = nbt.get(0);
            if(tag instanceof NBTTagFloat) sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, (int) (((NBTTagFloat) tag).getFloat()*multiply));
            else if(tag instanceof NBTTagDouble) sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, (int) (((NBTTagDouble) tag).getDouble()*multiply));
            else if(tag instanceof NBTPrimitive) sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, (int) (((NBTPrimitive) tag).getLong()*multiply));
            else if(tag instanceof NBTTagCompound) sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,((NBTTagCompound) tag).getSize());
            else if(tag instanceof NBTTagList) sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,((NBTTagList) tag).tagCount());
        }
        if(sender instanceof EntityPlayerMP){
            sender.sendMessage(translation("geocraft.command.atmosphere.data.query").done());
            NickelAPI.CHANNEL.sendTo(new PackageNBTInfo(nbt),(EntityPlayerMP) sender);
        }
    }

    static void modifyData(@Nonnull final ExecuteContext context) throws NickelRuntimeException {
        final IAtmosphereAccessor accessor = context.get(ACCESSOR);
        final AtmosphereData data = accessor.getAtmosphereDataHere();
        assert data != null;
        final NBTPath path = context.get("nbt_path");
        final NBTBase tag= context.get("nbt_tag");
        final NBTTagCompound compound = data.getSaveCompound();
        final ICommandSender sender = context.getSender();
        sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,path.set(compound,tag,false));
        translation("geocraft.command.atmosphere.data.set").color(TextFormatting.GREEN).sendTo(sender);
        if(sender instanceof EntityPlayerMP){
            final NBTPath subPath = path.subPath(path.length()-1);
            final NBTBase parent = subPath.resolve(compound).get(0);
            NickelAPI.CHANNEL.sendTo(new PackageNBTInfo(parent),(EntityPlayerMP) sender);
        }
    }

    static void track(@Nonnull final ExecuteContext context) throws CommandException{
        final int time = context.getInt("time");
        final String fileName = context.get("file_name");
        final BlockPos pos = context.getBlockPos(POS);
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final String name = context.get(PROPERTY);
        final Atmosphere atmosphere = context.get(ATMOSPHERE);
        if("temp".equalsIgnoreCase(name)){
            final InformationLoggingTracker tracker = createInformationTracker(atmosphere, TemperatureTracker::new,fileName, OrbTellusCraft.getLogger(),pos,time);
            context.notifyCommandListener("geocraft.command.atmosphere.track.temp",x,y,z,tracker.getId());
            return;
        }
        if("water".equalsIgnoreCase(name)){
            InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, OrbTellusCraft.getLogger(), OTCProperties.WATER,new BlockPos(x,y,z),time);
            context.notifyCommandListener("geocraft.command.atmosphere.track.water",x,y,z,tracker.getId());
            return;
        }
        if("steam".equalsIgnoreCase(name)){
            InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, OrbTellusCraft.getLogger(), OTCProperties.STEAM,new BlockPos(x,y,z),time);
            context.notifyCommandListener("geocraft.command.atmosphere.track.water",x,y,z,tracker.getId());
            return;
        }
        final ResourceLocation location = new ResourceLocation(name);
        final IGeographyProperty property= getProperty(location);
        if(property instanceof FluidProperty){
            InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, OrbTellusCraft.getLogger(),(FluidProperty) property,new BlockPos(x,y,z),time);
            context.notifyCommandListener("geocraft.command.atmosphere.track.gas",x,y,z,tracker.getId(),property.getRegistryName());
        }
        throw new CommandException("geocraft.command.atmosphere.property.unknown");
    }

    @Nonnull
    static IAtmosphereAccessor getAtmosphereAccessor(final @Nonnull World world,final @Nonnull BlockPos pos) throws CommandException {
        final IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,false);
        if(accessor == null){
            throw new CommandException("geocraft.command.atmosphere.nonexistent",pos);
        }
        return accessor;
    }

    @Nonnull
    static Atmosphere getAtmosphere(final @Nonnull IAtmosphereAccessor accessor) throws CommandException {
        final Atmosphere atmosphere = accessor.getAtmosphereHere();
        if(atmosphere ==null) throw new CommandException("geocraft.command.atmosphere.nonexistent",accessor.getPos());
        return atmosphere;
    }

    @Nonnull
    private static Layer getAtmosphereLayer(final Atmosphere atmosphere,final int height) throws CommandException {
        final Layer layer = atmosphere.getLayer(new BlockPos(0,height,0));
        if(layer == null){
            throw new CommandException("geocraft.command.atmosphere.layer.null");
        }
        return layer;
    }

    @Nonnull
    public static List<String> getPropertyList(){
        return GeographyProperty.MANAGER.getProperties().getKeys().stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }

    @Nonnull
    private static IGeographyProperty getProperty(final @Nonnull ResourceLocation location) throws CommandException {
        final IGeographyProperty property= GeographyProperty.MANAGER.getProperties().getValue(location);
        if(property == null){
            throw new CommandException("geocraft.command.atmosphere.property.not_found",location);
        }
        return property;
    }

    @Nonnull
    private static GeographyState getState(final @Nonnull IGeographyProperty property,final @Nonnull Layer layer) throws CommandException {
        final GeographyState state = layer.getState(property);
        if (state == null) {
            throw new CommandException("geocraft.command.atmosphere.property.null2", property.getRegistryName());
        }
        return state;
    }

    @Nonnull
    public static InformationLoggingTracker createInformationTracker(final @Nonnull Atmosphere atmosphere,
                                                                     final @Nonnull InformationLoggingTrackerFactory factory,
                                                                     final @Nonnull String fileName,
                                                                     final @Nonnull Logger logger,
                                                                     final @Nonnull BlockPos pos,
                                                                     final int time) throws CommandException {
        final InformationLoggingTracker tracker;
        try {
            tracker = factory.getInstance(new FileLogger(fileName,logger),pos,time);
            atmosphere.addTracker(tracker);
        } catch (final @Nonnull IOException e) {
            OrbTellusCraft.getLogger().error(e);
            throw new CommandException("geocraft.command.io_error",e.getMessage());
        }
        return tracker;
    }

    @Nonnull
    public static InformationLoggingTracker createFluidTracker(final @Nonnull Atmosphere atmosphere,
                                                               final @Nonnull String fileName,
                                                               final @Nonnull Logger logger,
                                                               final @Nonnull FluidProperty property,
                                                               final @Nonnull BlockPos pos,
                                                               final int time) throws CommandException {
        final InformationLoggingTracker tracker;
        try {
            tracker = new FluidTracker(new FileLogger(fileName,logger),property,pos,time);
            atmosphere.addTracker(tracker);
        } catch (IOException e) {
            OrbTellusCraft.getLogger().error(e);
            throw new CommandException("geocraft.command.io_error",e.getMessage());
        }
        return tracker;
    }

    @FunctionalInterface
    public interface InformationLoggingTrackerFactory {
        @Nonnull
        InformationLoggingTracker getInstance(@Nonnull FileLogger logger,@Nonnull BlockPos pos,int time);
    }

}
