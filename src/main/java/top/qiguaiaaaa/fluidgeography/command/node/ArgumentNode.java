package top.qiguaiaaaa.fluidgeography.command.node;

import top.qiguaiaaaa.fluidgeography.command.ArgumentType;

public class ArgumentNode extends CommandNode {
    protected String name;
    protected ArgumentType type;
    public ArgumentNode(String name,ArgumentType type){
        this.name = name;
        this.type = type;
    }
    @Override
    public boolean match(String args) {
        return type.match(args);
    }
}
