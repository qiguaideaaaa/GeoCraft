package top.qiguaiaaaa.fluidgeography.command;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.WaterTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.TemperatureTracker;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.ATMOSPHERE_ENERGY_RADIATION_LOSS_CONSTANT;

public class CommandAtmosphere extends ExtendedCommand {
    public static final String ATMOSPHERE_COMMAND_NAME = "atmosphere";
    public static final List<String> ALIASES = new ArrayList<>(Collections.singleton("大气"));
    public CommandAtmosphere(){
        this.registerSubCommand(new AddCommand(this));
        this.registerSubCommand(new SetCommand(this));
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
        return "fluidgeography.command.atmosphere.usage";
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
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic",pos.getX(),pos.getZ());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.1",atmosphere.getWaterAmount());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.2",atmosphere.getTemperature());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.4",atmosphere.getWindSpeed(pos));
        }
    }
    protected static Atmosphere getAtmosphere(World world,int x,int z) throws CommandException {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world,new BlockPos(x,63,z));
        if(atmosphere == null){
            throw new CommandException("fluidgeography.command.atmosphere.nonexistent.there",new Object());
        }
        return atmosphere;
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
            this.execute(server,world,pos,atmosphere,sender,args);
        }

        public abstract void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException;

        public static Chunk getValidChunk(World world,int x,int z) throws CommandException {
            BlockPos targetPos = new BlockPos(x,63,z);
            if(!world.isAreaLoaded(targetPos,1)){
                throw new CommandException("fluidgeography.command.chunk_error.unloaded",x,z);
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
            return "fluidgeography.command.atmosphere.set.usage";
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException{
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
            }
            if("water".equalsIgnoreCase(args[0])){
                atmosphere.setWaterAmount((int) value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.water",x,z,(int) value);
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                atmosphere.setTemperature((float) value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.temp",x,z, (float) value);
                return;
            }
            if("debug".equalsIgnoreCase(args[0])){
                if(value>0) notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.debug",x,z);
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "water","temp");
            } else if(args.length >= 3 && args.length <= 4){
                return getTabCompletionCoordinateXZ(args,2,targetPos);
            }
            return Collections.emptyList();
        }
    }
    public static class ResetCommand extends AtmosphereSubCommand{
        protected ResetCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException {
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
                    throw new CommandException("fluidgeography.command.chunk_error.unloaded",x,z);
                }
                Chunk chunk = world.getChunk(targetPos);
                atmosphere.resetTemperature(chunk);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.reset.temp",x,z, atmosphere.getTemperature());
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
            return "fluidgeography.command.atmosphere.reset.usage";
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
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException {
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
            }

            if("water".equalsIgnoreCase(args[0])){
                if(!atmosphere.addWaterAmount((int) value)){
                    throw new NumberInvalidException("commands.generic.num.tooSmall", value, -atmosphere.getWaterAmount());
                }
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.add.water",x,z,atmosphere.getWaterAmount());
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                atmosphere.addTemperature(value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.add.temp",x,z, atmosphere.getTemperature());
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "add";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "fluidgeography.command.atmosphere.add.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "water","temp");
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
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException {
            if(args.length<1) throw new WrongUsageException(getUsage(sender));
            if(args.length>4) throw new WrongUsageException("fluidgeography.command.atmosphere.query.usage.xyz");
            int x = pos.getX(),y=pos.getY(),z = pos.getZ();
            if("block_temp".equalsIgnoreCase(args[0])){
                if(args.length >=2 && args.length <4) throw new WrongUsageException("fluidgeography.command.atmosphere.query.usage.xyz");
                if(args.length == 4){
                    BlockPos pos1 = parseBlockPos(sender,args,1,false);
                    x = pos1.getX();
                    y = pos1.getY();
                    z = pos1.getZ();
                    atmosphere = getAtmosphere(world,x,z);
                }

                float temp = atmosphere.getTemperature(new BlockPos(x,y,z));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.block_temp",x,y,z,temp);
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)temp);
                return;
            }

            if(args.length>3 || args.length == 2) throw new WrongUsageException("fluidgeography.command.atmosphere.query.usage.xz");

            if(args.length == 3){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[1],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[2],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
            }
            if("water".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water",x,z,atmosphere.getWaterAmount());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,atmosphere.getWaterAmount());
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.temp",x,z, atmosphere.getTemperature());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.getTemperature());
                return;
            }
            if("basic_temp".equalsIgnoreCase(args[0])){
                Chunk chunk = getValidChunk(world,x,z);
                float tempBase = atmosphere.getTemperatureBase(chunk);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.basic_temp",x,z, tempBase);
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) tempBase);
                return;
            }
            if("wind".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.wind.north",x,z,atmosphere.getWindSpeed(EnumFacing.NORTH).dotProduct(new Vec3d(EnumFacing.NORTH.getDirectionVec())));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.wind.east",x,z,atmosphere.getWindSpeed(EnumFacing.EAST).dotProduct(new Vec3d(EnumFacing.EAST.getDirectionVec())));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.wind.south",x,z,atmosphere.getWindSpeed(EnumFacing.SOUTH).dotProduct(new Vec3d(EnumFacing.SOUTH.getDirectionVec())));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.wind.west",x,z,atmosphere.getWindSpeed(EnumFacing.WEST).dotProduct(new Vec3d(EnumFacing.WEST.getDirectionVec())));
                return;
            }
            if("underlying".equalsIgnoreCase(args[0])){
                Chunk chunk = getValidChunk(world,x,z);
                Underlying underlying = ChunkUtil.getUnderlying(chunk,63);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.underlying",x,z,underlying.heatCapacity,underlying.averageReflectivity,underlying.averageEmissivity);
                return;
            }
            if("heat_volume".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.heat_volume",x,z,atmosphere.getHeatCapacity());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.getHeatCapacity());
                return;
            }
            if("water_pressure".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water_pressure",x,z,AtmosphereUtil.getAtmosphereWaterPressure(atmosphere)*0.01);
                return;
            }
            if("emissivity".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water_pressure",x,z,AtmosphereUtil.getAtmosphereEmissivity(atmosphere));
                return;
            }
            if("biome".equalsIgnoreCase(args[0])){
                Chunk chunk = getValidChunk(world,x,z);
                Biome mainBiome = ChunkUtil.getMainBiome(chunk);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.basic_temp",x,z, mainBiome.getBiomeName());
                return;
            }
            if("average_height".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.basic_temp",x,z, atmosphere.getUnderlying().getAverageHeight());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.getUnderlying().getAverageHeight());
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
            return "fluidgeography.command.atmosphere.query.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                return getListOfStringsMatchingLastWord(args, "water","temp","basic_temp","block_temp","wind","underlying","heat_volume");
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
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException {
            if(args.length != 1) throw new WrongUsageException(getUsage(sender));
            WorldInfo info = world.getWorldInfo();
            if("sun".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.util.sun");
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.util.sun.1",
                        AtmosphereUtil.getSunHeightDegree(info));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.util.sun.2",
                        AtmosphereUtil.getSunEnergyPerChunk(info));
                return;
            }
            if("radiation".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.util.radiation",
                        pos.getX(),pos.getZ(),
                        AtmosphereUtil.getHeatEnergyRadiationLoss(atmosphere,AtmosphereUtil.getCloudInsulationEffect(atmosphere,info)));
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "util";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "fluidgeography.command.atmosphere.util.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1){
                return getListOfStringsMatchingLastWord(args, "sun","radiation");
            }
            return Collections.emptyList();
        }
    }
    public static class TrackCommand extends AtmosphereSubCommand{

        protected TrackCommand(ICommand father) {
            super(father);
        }

        @Override
        public void execute(MinecraftServer server, World world, BlockPos pos, Atmosphere atmosphere, ICommandSender sender, String[] args) throws CommandException {
            if(args.length<3 || args.length>5 || args.length == 4) throw new WrongUsageException(getUsage(sender));

            int time;
            String fileName = args[2];
            if(fileName.trim().isEmpty()) throw new WrongUsageException(getUsage(sender));
            int x = pos.getX(),z = pos.getZ();

            time = parseInt(args[1],1);
            if(args.length == 5){
                CoordinateArg coordinateArgX = parseCoordinate(pos.getX(),args[3],false);
                CoordinateArg coordinateArgZ = parseCoordinate(pos.getZ(),args[4],false);
                x = (int) coordinateArgX.getResult();
                z = (int) coordinateArgZ.getResult();
                atmosphere = getAtmosphere(world,x,z);
            }
            if("temp".equalsIgnoreCase(args[0])){
                InformationLoggingTracker tracker = createInformationTracker(atmosphere, TemperatureTracker::new,fileName, FGInfo.getLogger(),time);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.track.temp",x,z,tracker.getId());
                return;
            }
            if("water".equalsIgnoreCase(args[0])){
                InformationLoggingTracker tracker = createInformationTracker(atmosphere, WaterTracker::new,fileName, FGInfo.getLogger(),time);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.track.water",x,z,tracker.getId());
                return;
            }
            throw new WrongUsageException(getUsage(sender));
        }

        @Override
        public String getName() {
            return "track";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "fluidgeography.command.atmosphere.track.usage";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1){
                return getListOfStringsMatchingLastWord(args, "temp","water");
            } else if(args.length >= 4 && args.length <= 5){
                return getTabCompletionCoordinateXZ(args,3,targetPos);
            }
            return Collections.emptyList();
        }

        public static InformationLoggingTracker createInformationTracker(Atmosphere atmosphere, InformationLoggingTrackerFactory factory, String fileName, Logger logger, int time) throws CommandException {
            InformationLoggingTracker tracker;
            try {
                tracker = factory.getInstance(new FileLogger(fileName,logger),time);
                atmosphere.addListener(tracker);
            } catch (IOException e) {
                FGInfo.getLogger().error(e);
                throw new CommandException("fluidgeography.command.io_error",e.getMessage());
            }
            return tracker;
        }

        public interface InformationLoggingTrackerFactory {
            InformationLoggingTracker getInstance(FileLogger logger,int time);
        }
    }
}
