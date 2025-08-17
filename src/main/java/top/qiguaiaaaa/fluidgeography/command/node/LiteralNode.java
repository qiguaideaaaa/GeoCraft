package top.qiguaiaaaa.fluidgeography.command.node;

import top.qiguaiaaaa.fluidgeography.command.node.CommandNode;

public class LiteralNode extends CommandNode {
    protected String name;
    public LiteralNode(String name){
        this.name = name;
    }

    @Override
    public boolean match(String args) {
        return args.trim().equals(name);
    }
}
