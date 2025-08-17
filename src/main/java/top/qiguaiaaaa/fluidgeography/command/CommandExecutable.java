package top.qiguaiaaaa.fluidgeography.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface CommandExecutable {
    void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException ;
}
