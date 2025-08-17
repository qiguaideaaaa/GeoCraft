package top.qiguaiaaaa.fluidgeography.command.node;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;
import top.qiguaiaaaa.fluidgeography.command.CommandExecutable;

import java.util.ArrayList;

public abstract class CommandNode {
    protected ArrayList<LiteralNode> literals = new ArrayList<>();
    protected ArrayList<ArgumentNode> arguments = new ArrayList<>();
    protected CommandExecutable executable;
    protected String usage = "No usage found";
    /**
     * 子树
     * @param node 子命令
     * @return 自身
     */
    public CommandNode then(CommandNode node){
        addNode(node);
        return this;
    }

    /**
     * 设置运行节点
     * @param executable 运行节点
     * @return 自身
     */
    public CommandNode execute(CommandExecutable executable){
        this.executable =executable;
        return this;
    }
    public CommandNode usage(String msg){
        this.usage = msg;
        return this;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>0){
            CommandNode sub = getSubNode(args[0]);
            if(sub != null){
                sub.execute(server,sender, ArrayUtils.subarray(args,1,args.length));
                return;
            }
        }
        if(executable != null){
            executable.execute(server,sender, ArrayUtils.subarray(args,1,args.length));
            return;
        }
        throw new WrongUsageException(getUsage());
    }
    public String getUsage(){
        return this.usage;
    }
    protected void addNode(CommandNode node){
        if(node instanceof LiteralNode){
            literals.add((LiteralNode) node);
        } else if (node instanceof ArgumentNode) {
            arguments.add((ArgumentNode) node);
        }
    }
    protected CommandNode getSubNode(String args){
        for(LiteralNode node:literals){
            if(node.match(args)) return node;
        }
        for(ArgumentNode node:arguments){
            if(node.match(args)) return node;
        }
        return null;
    }
    public abstract boolean match(String args);
}
