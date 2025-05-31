package top.qiguaiaaaa.fluidgeography.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.*;

public abstract class ExtendedCommand extends CommandBase {
    protected Set<SubCommand> subCommands = new HashSet<>();

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>0){
            SubCommand sub = getSubCommand(args[0]);
            if(sub != null){
                sub.execute(server,sender, ArrayUtils.subarray(args,1,args.length));
                return;
            }
        }
        throw new WrongUsageException(getUsage(sender));
    }

    public void registerSubCommand(SubCommand command){
        if(command != null) subCommands.add(command);
    }
    public SubCommand getSubCommand(String arg){
        for(SubCommand command:subCommands){
            if(command.getName().equalsIgnoreCase(arg)) return command;
            for(String alise:command.getAliases()){
                if(alise.equalsIgnoreCase(arg)) return command;
            }
        }
        return null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length == 1){
            List<String> completions = new ArrayList<>();
            for(SubCommand command:subCommands){
                completions.add(command.getName());
                completions.addAll(command.getAliases());
            }
            return getListOfStringsMatchingLastWord(args,completions);
        }else if(args.length >1){
            SubCommand sub = getSubCommand(args[0]);
            if(sub != null){
                return sub.getTabCompletions(server,sender,ArrayUtils.subarray(args,1,args.length),targetPos);
            }
        }
        return Collections.emptyList();
    }
}
