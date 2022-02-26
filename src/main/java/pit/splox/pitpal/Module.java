package pit.splox.pitpal;

import net.minecraftforge.common.MinecraftForge;

public abstract class Module {
    private boolean isEnabled = false;
    public boolean alwaysEnabled = false;
    public void enable() {
        isEnabled = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        isEnabled = false;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
