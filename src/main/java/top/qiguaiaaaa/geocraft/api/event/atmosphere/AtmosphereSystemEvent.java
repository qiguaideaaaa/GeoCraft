package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 大气系统有关事件
 */
public class AtmosphereSystemEvent extends Event {
    private final WorldServer world;

    public AtmosphereSystemEvent(@Nonnull WorldServer world) {
        this.world = world;
    }
    @Nonnull
    public WorldServer getWorld() {
        return world;
    }

    /**
     * 当一个新的世界加载的时候，会发布该事件以获取对应的大气系统
     */
    @Cancelable
    public static class Create extends AtmosphereSystemEvent{
        private IAtmosphereSystem systemToBeUsed;

        public Create(@Nonnull WorldServer world) {
            super(world);
        }

        /**
         * 获取目前被设置的大气系统
         * @return 将被使用的大气系统
         */
        @Nullable
        public IAtmosphereSystem getSystem() {
            return systemToBeUsed;
        }

        /**
         * 设置被使用的大气系统
         * @param systemToBeUsed 将被使用的大气系统
         */
        public void setSystem(@Nullable IAtmosphereSystem systemToBeUsed) {
            this.systemToBeUsed = systemToBeUsed;
        }
    }
}
