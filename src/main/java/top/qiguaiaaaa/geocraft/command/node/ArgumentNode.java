package top.qiguaiaaaa.geocraft.command.node;

import top.qiguaiaaaa.geocraft.command.ArgumentType;

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
