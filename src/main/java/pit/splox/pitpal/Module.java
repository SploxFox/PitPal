package pit.splox.pitpal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

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

    public Property getConfigProperty(String key, String defaultValue, Property.Type type) {
        return getConfig().getOrDefault(key, new Property(key, defaultValue, type));
    }

    public int getConfigInt(String key, int defaultValue) {
        return getGlobalConfig().get(Configuration.CATEGORY_GENERAL + ".modules." + getClass().getSimpleName(), key, defaultValue).getInt(defaultValue);
        //return getConfigProperty(key, "" + defaultValue, Type.INTEGER).getInt(defaultValue);
    }
}
