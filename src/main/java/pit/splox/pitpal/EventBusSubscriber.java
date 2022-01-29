package pit.splox.pitpal;

import net.minecraftforge.common.MinecraftForge;

public abstract class EventBusSubscriber {
    public EventBusSubscriber() {
        PitPal.onInit(this::init);
    }
    private void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
