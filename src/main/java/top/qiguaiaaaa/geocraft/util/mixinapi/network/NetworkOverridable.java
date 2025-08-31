package top.qiguaiaaaa.geocraft.util.mixinapi.network;

import net.minecraft.network.PacketBuffer;

public interface NetworkOverridable {
    void networkWrite(PacketBuffer buf);
}
