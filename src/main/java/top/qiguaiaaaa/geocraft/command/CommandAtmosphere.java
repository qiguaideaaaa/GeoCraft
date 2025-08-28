package top.qiguaiaaaa.geocraft.command;

import net.minecraft.block.material.Material;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.geocraft.api.GEOProperties;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.InformationLoggingTracker;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.atmosphere.GeographyPropertyManager;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphere;
import top.qiguaiaaaa.geocraft.atmosphere.layer.Underlying;
import top.qiguaiaaaa.geocraft.atmosphere.tracker.FluidTracker;
import top.qiguaiaaaa.geocraft.atmosphere.tracker.TemperatureTracker;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandAtmosphere extends ExtendedCommand {
    public static final String ATMOSPHERE_COMMAND_NAME = "atmosphere";
    public static final List<String> ALIASES = new ArrayList<>(Collections.singleton("大气"));
    public CommandAtmosphere(){
        this.registerSubCommand(new AddCommand(this));
        this.registerSubCommand(new SetCommand(this));
        this.registerSubCommand(new StopCommand(this));
        this.registerSubCommand(new ResetCommand(this));
        this.registerSubCommand(new QueryCommand(this));
        this.registerSubCommand(new UtilCommand(this));
        this.registerSubCommand(new TrackCommand(this));
    }
    @Override
    public String getName() {
        return ATMOSPHERE_COMMAND_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "geocraft.command.atmosphere.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return ALIASES;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException  {
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();
        if(args.length >= 1){
            super.execute(server,sender,args);
        } else{
            Atmosphere atmosphere = getAtmosphere(world,pos.getX(),pos.getZ());
            notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic",TextFormatting.AQUA,pos.getX(),Altitude.get物理海拔(pos.getY()),pos.getZ());
            notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic.1",
                    atmosphere.getPressure(pos),
                    atmosphere.getWaterPressure(pos),
                    atmosphere.getAtmosphereTemperature(pos)
            );
            notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic.2",atmosphere.getWind(pos));
            for(Layer layer = atmosphere.getBottomLayer(); layer != null; layer = layer.getUpperLayer()){
                notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic.3",
                        layer.getTagName(),layer.getBeginY(),layer.getBeginY()+layer.getDepth());
                notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic.4",layer.getTemperature());
                FluidState steam = (layer instanceof AtmosphereLayer)?((AtmosphereLayer)layer).getSteam():null;
                FluidState water = layer.getWater();
                notifyCommandListener(sender,this, "geocraft.command.atmosphere.query.basic.5",
                        steam==null?"NULL":steam,water==null?"NULL":water);
            }

        }
    }
    protected static Atmosphere getAtmosphere(World world, int x, int z) throws CommandException {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world,new BlockPos(x,63,z));
        if(atmosphere == null){
            throw new CommandException("geocraft.command.atmosphere.nonexistent.there",new Object());
        }
        return atmosphere;
    }
    protected static Layer getAtmosphereLayer(Atmosphere atmosphere,int height) throws CommandException {
        Layer layer = atmosphere.getLayer(new BlockPos(0,height,0));
        if(layer == null){
            throw new CommandException("geocraft.command.atmosphere.layer.null");
        }
        return layer;
    }
    protected static List<String> getPropertyList(){
        Set<ResourceLocation> locations = GeographyPropertyManager.getProperties().getKeys();
        List<String> strings = new ArrayList<>();
        for(ResourceLocation location:locations){
            strings.add(location.toString());
        }
        return strings;
    }
    protected static GeographyProperty getProperty(ResourceLocation location) throws CommandException {
        GeographyProperty property= GeographyPropertyManager.getProperties().getValue(location);
        if(property == null){
            throw new CommandException("geocraft.command.atmosphere.property.not_found",location);
        }
        return property;
    }
    protected static GeographyState getState(GeographyProperty property, Layer layer) throws CommandException {
        GeographyState state = layer.getState(property);
        if(state == null){
            throw new CommandException("geocraft.command.atmosphere.property.null2",property.getRegistryName());
        }
        return state;
    }
    public static abstract class AtmosphereSubCommand extends SubCommand{

        protected AtmosphereSubCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            World world = sender.getEntityWorld();
            BlockPos pos = sender.getPosition();
            Atmosphere atmosphere = getAtmosphere(world,pos.getX(),pos.getZ());
            Layer layer = getAtmosphereLayer(atmosphere,pos.getY());
            this.execute(server,world,pos,atmosphere,layer,sender,args);
        }

        public abstract void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) throws CommandException;

        public static Chunk getValidChunk(World world,int x,int z) throws CommandException {
            BlockPos targetPos = new BlockPos(x,63,z);
            if(!world.isAreaLoaded(targetPos,1)){
                throw new CommandException("geocraft.command.chunk_error.unloaded",x,z);
            }
            return world.getChunk(targetPos);
        }
    }
    public static class SetCommand extends AtmosphereSubCommand{

        protected SetCommand(ICommand father) {
            super(father);
        }

        @Override
        public String getName() {
            return "set";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.set.usage";
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) throws CommandException{
            if(args.length<2 || args.length>4 || args.length == 3) throw new WrongUsageException(getUsage(sender));

            double value;
            int x = pos.getX(),z = pos.getZ();
            value = parseDouble(args[1],0);

            if(args.length == 4){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[2],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[3],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
                layer = getAtmosphereLayer(atmosphere,pos.getY());
            }
            notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.layer_inf",layer.getTagName(),
                    layer.getBeginY(),layer.getBeginY()+layer.getDepth());
            if("steam".equalsIgnoreCase(args[0])){
                FluidState steam = (layer instanceof AtmosphereLayer)?((AtmosphereLayer)layer).getSteam():null;
                if(steam == null){
                    throw new CommandException("geocraft.command.atmosphere.property.null");
                }
                steam.setAmount((int)value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.steam",x,pos.getY(),z,(int) value);
                return;
            }
            if("water".equalsIgnoreCase(args[0])){
                FluidState water = layer.getWater();
                if(water == null){
                    throw new CommandException("geocraft.command.atmosphere.property.null");
                }
                water.setAmount((int)value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.water",x,pos.getY(),z,(int) value);
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                layer.getTemperature().set((float) value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.temp",x,pos.getY(),z, (float) value);
                return;
            }
            if("debug".equalsIgnoreCase(args[0])){
                if(!(atmosphere instanceof DefaultAtmosphere)){
                    throw new CommandException("geocraft.command.atmosphere.unknown");
                }
                DefaultAtmosphere a = (DefaultAtmosphere) atmosphere;
                a.setDebug(value>0);
                if(value>0) notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.debug",x,z,a.isDebug());
                return;
            }
            ResourceLocation location = new ResourceLocation(args[0]);
            GeographyProperty property= getProperty(location);
            GeographyState state = getState(property,layer);
            if(state instanceof FluidState){
                FluidState gas = (FluidState) state;
                gas.setAmount((int) value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.gas",x,pos.getY(),z,gas.getAmount());
                return;
            }
            if(state instanceof TemperatureState){
                TemperatureState temperature = (TemperatureState) state;
                temperature.set((float) value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.set.temp2",x,pos.getY(),z,temperature.get());
                return;
            }
            throw new CommandException("geocraft.command.atmosphere.property.unknown");
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                List<String> res = getPropertyList();
                res.addAll(getListOfStringsMatchingLastWord(args, "water","temp","ground_temp"));
                return res;
            } else if(args.length >= 3 && args.length <= 4){
                return getTabCompletionCoordinateXZ(args,2,targetPos);
            }
            return Collections.emptyList();
        }
    }
    public static class StopCommand extends AtmosphereSubCommand {
        protected StopCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            World world = sender.getEntityWorld();
            IAtmosphereSystem system = AtmosphereSystemManager.getAtmosphereSystem(world);
            if(system == null) throw new CommandException("geocraft.command.atmosphere_system.null");
            system.setStop(!system.isStopped());
            notifyCommandListener(sender, this, "geocraft.command.atmosphere.reset.temp",system.isStopped() , 0, 0);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) {}
        @Override
        public String getName() {
            return "stop";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/atmosphere stop";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "stop");
            }
            return Collections.emptyList();
        }
    }

    public static class ResetCommand extends AtmosphereSubCommand{
        protected ResetCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer,ICommandSender sender, String[] args) throws CommandException {
            if(args.length<1 || args.length>3 || args.length == 2) throw new WrongUsageException(getUsage(sender));

            int x = pos.getX(),z = pos.getZ();

            if(args.length == 3){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[1],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[2],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
            }
            if("temp".equalsIgnoreCase(args[0])){
                BlockPos targetPos = new BlockPos(x,63,z);
                if(!world.isAreaLoaded(targetPos,1)){
                    throw new CommandException("geocraft.command.chunk_error.unloaded",x,z);
                }
                Chunk chunk = world.getChunk(targetPos);
                if(atmosphere instanceof DefaultAtmosphere){
                    ((DefaultAtmosphere)atmosphere).重置温度(chunk);
                }else throw new CommandException("geocraft.command.atmosphere.unknown");
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.reset.temp",x,z, atmosphere.getAtmosphereTemperature(pos));
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "reset";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.reset.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "temp");
            } else if(args.length >= 2 && args.length <= 3){
                return getTabCompletionCoordinateXZ(args,3,targetPos);
            }
            return Collections.emptyList();
        }
    }
    public static class AddCommand extends AtmosphereSubCommand{

        protected AddCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer,ICommandSender sender, String[] args) throws CommandException {
            if(args.length<2 || args.length>4 || args.length == 3) throw new WrongUsageException(getUsage(sender));

            double value;
            int x = pos.getX(),z = pos.getZ();
            value = parseDouble(args[1]);

            if(args.length == 4){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[2],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[3],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
                layer = getAtmosphereLayer(atmosphere,pos.getY());
            }
            notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.layer_inf",layer.getTagName(),
                    layer.getBeginY(),layer.getBeginY()+layer.getDepth());
            if("steam".equalsIgnoreCase(args[0])){
                FluidState steam = (layer instanceof AtmosphereLayer)?((AtmosphereLayer)layer).getSteam():null;
                if(steam == null) throw new CommandException("geocraft.command.atmosphere.property.null");
                if(!steam.addAmount((int) value)){
                    throw new NumberInvalidException("commands.generic.num.tooSmall", value, -steam.getAmount());
                }
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.water",x,pos.getY(),z,steam);
                return;
            }
            if("water".equalsIgnoreCase(args[0])){
                FluidState water = layer.getWater();
                if(water == null) throw new CommandException("geocraft.command.atmosphere.property.null");
                if(!water.addAmount((int) value)){
                    throw new NumberInvalidException("commands.generic.num.tooSmall", value, -water.getAmount());
                }
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.water",x,pos.getY(),z,water);
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                layer.getTemperature().add(value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.temp",x,pos.getY(),z, layer.getTemperature());
                return;
            }
            if("heat".equalsIgnoreCase(args[0])){
                layer.putHeat(value,new BlockPos(x,pos.getY(),z));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.temp",x,pos.getY(),z, layer.getTemperature());
                return;
            }
            ResourceLocation location = new ResourceLocation(args[0]);
            GeographyProperty property= getProperty(location);
            GeographyState state = getState(property,layer);
            if(state instanceof FluidState){
                FluidState gas = (FluidState) state;
                gas.addAmount((int) value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.gas",x,pos.getY(),z,gas.getAmount());
                return;
            }
            if(state instanceof TemperatureState){
                TemperatureState temperature = (TemperatureState) state;
                temperature.add(value);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.add.temp2",x,pos.getY(),z,temperature.get());
                return;
            }
            throw new CommandException("geocraft.command.atmosphere.property.unknown");
        }

        @Override
        public String getName() {
            return "add";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.add.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                List<String> res = getPropertyList();
                res.addAll(getListOfStringsMatchingLastWord(args, "steam","water","temp","heat"));
                return res;
            } else if(args.length >= 3 && args.length <= 4){
                return getTabCompletionCoordinateXZ(args,3,targetPos);
            }
            return Collections.emptyList();
        }
    }
    public static class QueryCommand extends AtmosphereSubCommand{

        protected QueryCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) throws CommandException {
            if(args.length<1) throw new WrongUsageException(getUsage(sender));
            if(args.length>4) throw new WrongUsageException("geocraft.command.atmosphere.query.usage.xyz");
            int x = pos.getX(),y=pos.getY(),z = pos.getZ();
            if("block_temp".equalsIgnoreCase(args[0])){
                if(args.length >=2 && args.length <4) throw new WrongUsageException("geocraft.command.atmosphere.query.usage.xyz");
                if(args.length == 4){
                    BlockPos pos1 = parseBlockPos(sender,args,1,false);
                    x = pos1.getX();
                    y = pos1.getY();
                    z = pos1.getZ();
                    atmosphere = getAtmosphere(world,x,z);
                }

                float temp = atmosphere.getTemperature(new BlockPos(x,y,z),world.getBlockState(new BlockPos(x,y,z)).getMaterial() != Material.AIR);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.block_temp",x,y,z,temp);
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)temp);
                return;
            }

            if(args.length>3 || args.length == 2) throw new WrongUsageException("geocraft.command.atmosphere.query.usage.xz");

            if(args.length == 3){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[1],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[2],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
                layer = getAtmosphereLayer(atmosphere,pos.getY());
            }
            notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.layer_inf",layer.getTagName(),
                    layer.getBeginY(),layer.getBeginY()+layer.getDepth());
            if("steam".equalsIgnoreCase(args[0])){
                FluidState steam = (layer instanceof AtmosphereLayer)?((AtmosphereLayer)layer).getSteam():null;
                if(steam == null) throw new CommandException("geocraft.command.atmosphere.property.null");
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.steam",x,y,z,steam);
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,steam.getAmount());
                return;
            }
            if("water".equalsIgnoreCase(args[0])){
                FluidState water = layer.getWater();
                if(water == null) throw new CommandException("geocraft.command.atmosphere.property.null");
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.water",x,y,z,water);
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,water.getAmount());
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.temp",x,y,z,layer.getTemperature() );
                return;
            }
            if("ground_temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.ground_temp",x,z,atmosphere.getUnderlying().getTemperature());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.getUnderlying().getTemperature().get());
                return;
            }
            if("wind".equalsIgnoreCase(args[0])){
                Vec3d wind = atmosphere.getWind(pos);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.north",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.NORTH.getDirectionVec())));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.east",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.EAST.getDirectionVec())));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.south",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.SOUTH.getDirectionVec())));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.west",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.WEST.getDirectionVec())));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.west",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.UP.getDirectionVec())));
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.wind.west",x,y,z,wind.dotProduct(new Vec3d(EnumFacing.DOWN.getDirectionVec())));
                return;
            }
            if("underlying".equalsIgnoreCase(args[0])){
                UnderlyingLayer underlying1 = atmosphere.getUnderlying();
                Underlying underlying;
                if(underlying1 instanceof Underlying){
                    underlying = (Underlying) underlying1;
                }else throw new CommandException("geocraft.command.atmosphere.unknown_underlying");
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.underlying",x,underlying.getAltitude(),z,underlying.getHeatCapacity(),underlying.平均返照率);
                return;
            }
            if("heat_volume".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.heat_volume",x,y,z,layer.getHeatCapacity());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) layer.getHeatCapacity());
                return;
            }
            if("water_pressure".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.water_pressure",x,y,z,atmosphere.getWaterPressure(pos)*0.01);
                return;
            }
            if("pressure".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.query.pressure",x,y,z,atmosphere.getPressure(pos)*0.01);
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "query";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.query.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "water","steam","temp","water_pressure","pressure","ground_temp","block_temp","wind","underlying","heat_volume");
            }if(args.length == 4) {
                if ("block_temp".equals(args[0])) {
                    return getTabCompletionCoordinate(args, 2, targetPos);
                }
            }else if(args.length >= 2 && args.length <= 3){
                if("block_temp".equals(args[0])){
                    return getTabCompletionCoordinate(args,2,targetPos);
                }
                return getTabCompletionCoordinateXZ(args,2,targetPos);
            }
            return Collections.emptyList();
        }
    }
    public static class UtilCommand extends AtmosphereSubCommand{
        protected UtilCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) throws CommandException {
            if(args.length != 1) throw new WrongUsageException(getUsage(sender));
            WorldInfo info = world.getWorldInfo();
            if("sun".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.util.sun");
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.util.sun.1",
                        AtmosphereUtil.getSunHeight(info).getDegree());
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.util.sun.2",
                        AtmosphereUtil.getSunEnergyPerChunk(info));
                return;
            }
            if("property".equalsIgnoreCase(args[0])){
                IForgeRegistry<GeographyProperty> registry = GeographyPropertyManager.getProperties();
                for(GeographyProperty property:registry){
                    notifyCommandListener(sender,this,"geocraft.command.atmosphere.util.property",property.getRegistryName());
                }
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "util";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.util.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1){
                return getListOfStringsMatchingLastWord(args, "sun","property");
            }
            return Collections.emptyList();
        }
    }
    public static class TrackCommand extends AtmosphereSubCommand{

        protected TrackCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere,Layer layer, ICommandSender sender, String[] args) throws CommandException {
            if(args.length<3 || args.length>6 || args.length == 4 || args.length == 5) throw new WrongUsageException(getUsage(sender));

            int time;
            String fileName = args[2];
            if(fileName.trim().isEmpty()) throw new WrongUsageException(getUsage(sender));
            int x = pos.getX(),y = pos.getY(),z = pos.getZ();

            time = parseInt(args[1],1);
            if(args.length == 6){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[3],false);
                CoordinateArg coordinateArgY = parseCoordinate(pos.getY(),args[4],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[5],false);
                x = (int) coordinateArgX.getResult();
                y = (int) coordinateArgY.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
            }
            if("temp".equalsIgnoreCase(args[0])){
                InformationLoggingTracker tracker = createInformationTracker(atmosphere, TemperatureTracker::new,fileName, GEOInfo.getLogger(),new BlockPos(x,y,z),time);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.track.temp",x,y,z,tracker.getId());
                return;
            }
            if("water".equalsIgnoreCase(args[0])){
                InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, GEOInfo.getLogger(), GEOProperties.WATER,new BlockPos(x,y,z),time);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.track.water",x,y,z,tracker.getId());
                return;
            }
            if("steam".equalsIgnoreCase(args[0])){
                InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, GEOInfo.getLogger(), GEOProperties.STEAM,new BlockPos(x,y,z),time);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.track.water",x,y,z,tracker.getId());
                return;
            }
            ResourceLocation location = new ResourceLocation(args[0]);
            GeographyProperty property= getProperty(location);
            if(property instanceof FluidProperty){
                InformationLoggingTracker tracker = createFluidTracker(atmosphere,fileName, GEOInfo.getLogger(),(FluidProperty) property,new BlockPos(x,y,z),time);
                notifyCommandListener(sender,this,"geocraft.command.atmosphere.track.gas",x,y,z,tracker.getId(),property.getRegistryName());
            }
            throw new CommandException("geocraft.command.atmosphere.property.unknown");
        }

        @Override
        public String getName() {
            return "track";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "geocraft.command.atmosphere.track.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1){
                List<String> res = getPropertyList();
                res.addAll(getListOfStringsMatchingLastWord(args, "temp","water","steam"));
                return res;
            } else if(args.length >= 4 && args.length <= 5){
                return getTabCompletionCoordinate(args,3,targetPos);
            }
            return Collections.emptyList();
        }

        public static InformationLoggingTracker createInformationTracker(Atmosphere atmosphere, InformationLoggingTrackerFactory factory, String fileName, Logger logger,BlockPos pos, int time) throws CommandException {
            InformationLoggingTracker tracker;
            try {
                tracker = factory.getInstance(new FileLogger(fileName,logger),pos,time);
                atmosphere.addTracker(tracker);
            } catch (IOException e) {
                GEOInfo.getLogger().error(e);
                throw new CommandException("geocraft.command.io_error",e.getMessage());
            }
            return tracker;
        }
        public static InformationLoggingTracker createFluidTracker(Atmosphere atmosphere, String fileName, Logger logger, FluidProperty property, BlockPos pos, int time) throws CommandException {
            InformationLoggingTracker tracker;
            try {
                tracker = new FluidTracker(new FileLogger(fileName,logger),property,pos,time);
                atmosphere.addTracker(tracker);
            } catch (IOException e) {
                GEOInfo.getLogger().error(e);
                throw new CommandException("geocraft.command.io_error",e.getMessage());
            }
            return tracker;
        }

        public interface InformationLoggingTrackerFactory {
            InformationLoggingTracker getInstance(FileLogger logger,BlockPos pos,int time);
        }
    }
}
