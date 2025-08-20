package top.qiguaiaaaa.fluidgeography.command;

import net.minecraft.block.material.Material;
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
import top.qiguaiaaaa.fluidgeography.atmosphere.listener.WaterTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.atmosphere.listener.TemperatureTracker;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.*;

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
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic",pos.getX(),Altitude.get物理海拔(pos.getY()),pos.getZ());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.1",atmosphere.get水量());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.2",atmosphere.get低层大气温度());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.3",atmosphere.get地表温度());
            notifyCommandListener(sender,this, "fluidgeography.command.atmosphere.query.basic.4",atmosphere.getAtmosphereWorldInfo().getModel().getWind(atmosphere,pos));
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
                atmosphere.set水量((int) value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.water",x,z,(int) value);
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                atmosphere.set低层大气温度((float) value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.temp",x,z, (float) value);
                return;
            }
            if("ground_temp".equalsIgnoreCase(args[0])){
                atmosphere.set地表温度((float) value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.set.ground_temp",x,z, (float) value);
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
                return getListOfStringsMatchingLastWord(args, "water","temp","ground_temp");
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
                atmosphere.重置温度(chunk);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.reset.temp",x,z, atmosphere.get低层大气温度());
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
                if(!atmosphere.add水量((int) value)){
                    throw new NumberInvalidException("commands.generic.num.tooSmall", value, -atmosphere.get水量());
                }
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.add.water",x,z,atmosphere.get水量());
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                atmosphere.add低层大气温度(value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.add.temp",x,z, atmosphere.get低层大气温度());
                return;
            }
            if("ground_temp".equalsIgnoreCase(args[0])){
                atmosphere.add地表温度(value);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.add.ground_temp",x,z, atmosphere.get地表温度());
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
                return getListOfStringsMatchingLastWord(args, "water","temp","ground_temp");
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

                float temp = atmosphere.get温度(new BlockPos(x,y,z),world.getBlockState(new BlockPos(x,y,z)).getMaterial() == Material.AIR);
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
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water",x,z,atmosphere.get水量());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,atmosphere.get水量());
                return;
            }
            if("temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.temp",x,z, atmosphere.get低层大气温度());
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.ground_temp",x,z, atmosphere.get地表温度());
                return;
            }
            if("test".equalsIgnoreCase(args[0])){
                WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
                Underlying 下垫面 = atmosphere.get下垫面();
                double 太阳辐射透过率 = atmosphere.getAtmosphereWorldInfo().getModel().get大气透过率(atmosphere);
                double 太阳短波辐射 = getSunEnergyPerChunk(worldInfo)*(1-下垫面.平均返照率)*太阳辐射透过率;

                notifyCommandListener(sender,this, String.format("Sun radiation %f Q = %f * %f * %f", 太阳短波辐射,getSunEnergyPerChunk(worldInfo),(1-下垫面.平均返照率),太阳辐射透过率));

                // 地面长波辐射
                double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
                double 地面长波辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get地表温度(), 4) * 下垫面.平均发射率*地面辐射损失系数;

                notifyCommandListener(sender,this,"Ground radiation:"+地面长波辐射+" Q");

                // 云层和大气的辐射
                double 吸收系数 = 0.01;
                double 云量 = 1-太阳辐射透过率;
                double 云层回辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 云量;
                double 云层高度 = 1500;
                double 云层回辐射到达地面比例 = 0.5 * Math.exp(
                        -吸收系数 *
                                AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度)
                                * 云层高度)
                        * (1 - 0.5 * 云量);

                notifyCommandListener(sender,this, String.format("Cloud radiation %f Q = %f * 0.5 * Math.exp(-%f * %f * %f )*(1-0.5* %f )", 云层回辐射*云层回辐射到达地面比例,云层回辐射,吸收系数,AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度),云层高度,云量));

                // 大气辐射
                double 大气发射率 = atmosphere.getAtmosphereWorldInfo().getModel().get低层大气发射率(atmosphere);
                double 大气辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 大气发射率 * (1.0 - 云量);
                double 大气回辐射到达地面比例 = 0.5 * Math.exp(
                        -吸收系数 *
                                AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+ FinalFactors.低层大气厚度)
                                * 云层高度)
                        * FinalFactors.低层大气厚度
                        * (1 - 0.5 * 云量);
                notifyCommandListener(sender,this, String.format("Atmosphere radiation %f Q = %f * 0.5 * Math.exp(-%f * %f * %f )*(1-0.5* %f )",大气辐射*大气回辐射到达地面比例,大气辐射,吸收系数,AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度),云层高度,云量));

                double 地面净辐射损失 = (地面长波辐射 - (云层回辐射*云层回辐射到达地面比例 + 大气辐射*大气回辐射到达地面比例));
                double 大气净辐射损失 = 云层回辐射+大气辐射-
                        地面长波辐射*AtmosphereUtil.大气吸收系数(
                                FinalFactors.低层大气厚度,
                                get低层大气平均密度(下垫面.get地面平均海拔().get物理海拔()),
                                1);
                notifyCommandListener(sender,this, String.format("Ground radiation loss (%f K) %f Q = (%f - (%f + %f))",地面净辐射损失/atmosphere.get下垫面().热容,地面净辐射损失,地面长波辐射,云层回辐射*云层回辐射到达地面比例,大气辐射*大气回辐射到达地面比例));
                notifyCommandListener(sender,this, String.format("Atmosphere radiation loss (%f K) %f Q = %f + %f - %f*%f",大气净辐射损失/atmosphere.get低层大气热容(),大气净辐射损失,云层回辐射,大气辐射,地面长波辐射,AtmosphereUtil.大气吸收系数(
                        FinalFactors.低层大气厚度,
                        get低层大气平均密度(下垫面.get地面平均海拔().get物理海拔()),
                        1)));
                return;
            }
            if("low_temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.temp",x,z, atmosphere.get低层大气温度());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.get低层大气温度());
                return;
            }
            if("ground_temp".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.ground_temp",x,z, atmosphere.get地表温度());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.get地表温度());
                return;
            }
            if("basic_temp".equalsIgnoreCase(args[0])){
                Chunk chunk = getValidChunk(world,x,z);
                float tempBase = atmosphere.getAtmosphereWorldInfo().getModel().getInitTemperature(atmosphere,chunk);
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
                Underlying underlying = Underlying.getUnderlying(chunk,new Altitude(63));
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.underlying",x,atmosphere.get下垫面().get地面平均海拔().toString(),z,underlying.热容,underlying.平均返照率,underlying.平均发射率);
                return;
            }
            if("heat_volume".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.heat_volume",x,z,atmosphere.get低层大气热容());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.get低层大气热容());
                return;
            }
            if("water_pressure".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water_pressure",x,z,AtmosphereUtil.get大气水汽压(atmosphere)*0.01);
                return;
            }
            if("emissivity".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.water_pressure",x,z,atmosphere.getAtmosphereWorldInfo().getModel().get低层大气发射率(atmosphere));
                return;
            }
            if("biome".equalsIgnoreCase(args[0])){
                Chunk chunk = getValidChunk(world,x,z);
                Biome mainBiome = ChunkUtil.getMainBiome(chunk);
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.basic_temp",x,z, mainBiome.getBiomeName());
                return;
            }
            if("average_height".equalsIgnoreCase(args[0])){
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.query.basic_temp",x,z, atmosphere.get下垫面().toString());
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) atmosphere.get下垫面().get地面平均海拔().get());
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
                return getListOfStringsMatchingLastWord(args, "water","temp","low_temp","ground_temp","basic_temp","block_temp","wind","underlying","heat_volume");
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
                        AtmosphereUtil.getSunHeight(info).getDegree());
                notifyCommandListener(sender,this,"fluidgeography.command.atmosphere.util.sun.2",
                        AtmosphereUtil.getSunEnergyPerChunk(info));
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
                return getListOfStringsMatchingLastWord(args, "sun");
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
