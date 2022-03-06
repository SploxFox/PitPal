package pit.splox.pitpal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import static pit.splox.pitpal.PitPal.*;

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

    public ConfigCategory getConfig() {
        return getGlobalConfig().getCategory(Configuration.CATEGORY_GENERAL + ".modules." + getClass().getSimpleName());
    }
}
