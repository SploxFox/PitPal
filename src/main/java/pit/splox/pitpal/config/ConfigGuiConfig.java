package pit.splox.pitpal.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import pit.splox.pitpal.PitPal;

public class ConfigGuiConfig extends GuiConfig {
    public ConfigGuiConfig(GuiScreen parent) {
        super(parent,
            new ConfigElement(PitPal.getConfig().getCategory("general")).getChildElements(),
            PitPal.MODID,
            false,
            false,
            "PitPal Config");
        
        titleLine2 = PitPal.getConfig().getConfigFile().getAbsolutePath();
    }
}
