package top.qiguaiaaaa.fluidgeography.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class SubCommand extends ExtendedCommand {
    protected final ICommand father;

    protected SubCommand(ICommand father) {
        this.father = father;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return this.father.checkPermission(server,sender);
    }

    @Override
    public int getRequiredPermissionLevel() {
        if(father instanceof CommandBase) return ((CommandBase) father).getRequiredPermissionLevel();
        return super.getRequiredPermissionLevel();
    }
}
