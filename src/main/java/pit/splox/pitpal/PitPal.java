package pit.splox.pitpal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pit.splox.pitpal.modules.gui.PpRenderItem;
import pit.splox.pitpal.pit_splox.PitSploxApi;
import pit.splox.pitpal.pit_splox.User;
import pit.splox.pitpal.pit_state.PitState;

@SideOnly(Side.CLIENT)
@Mod(modid = PitPal.MODID, version = PitPal.VERSION, name = PitPal.NAME, clientSideOnly = true, canBeDeactivated = true, guiFactory = "pit.splox.pitpal.config.ConfigGuiFactory")
public class PitPal {
    public PitPal() {
        pitPalInstance = this;
    }
    private static PitPal pitPalInstance;
    public static PitPal getInstance() {
        return pitPalInstance;
    }

    /**
     * Info about the mod.
     */
    public static final String MODID = "PitPal";
    public static final String NAME = "PitPal";
    public static final String VERSION = "@VERSION@";

    // Global constants and utils
    /**
     * PitPal logger
     */
    public static final Logger logger = LogManager.getLogger(PitPal.MODID);
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static FontRenderer getFontRenderer() {
        return mc.fontRendererObj;
    }
    
    public static final GsonBuilder gsonBuilder = new GsonBuilder();
    public static final Gson gson = gsonBuilder.create();
    public static final JsonParser jsonParser = new JsonParser();
    public static final Reflections reflections = new Reflections("pit.splox.pitpal");

    public static final PitState state = new PitState();

    public PpRenderItem ppRenderItem;
    public final User user = new User();
    private final Map<String, Module> modules = new HashMap<String, Module>();

    private Configuration config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        reflections.getSubTypesOf(Module.class).forEach(c -> {
            try {
                String name = c.getSimpleName();
                Module module = (Module) c.newInstance();
                module.alwaysEnabled = c.isAnnotationPresent(AlwaysEnabled.class);
                modules.put(name, module);

                
            } catch (Exception e) {
                logger.error("Failed to instantiate module " + c.getName());
                e.printStackTrace();
            }
        });

        handleConfigChanged();

        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        triggerInitListeners();
        logger.info("Initialized pitpal.");

        ppRenderItem = PpRenderItem.init();

        hasInitialized = true;

        PitSploxApi.authenticate();
    }

    private static boolean hasInitialized = false;

    private void _handleConfigChanged() {
        modules.forEach((name, module) -> {
            System.out.println(name + " " + module.alwaysEnabled);
            if (module.alwaysEnabled) {
                module.enable();
                return;
            }
            
            boolean isEnabled = config.get(Configuration.CATEGORY_GENERAL + ".modules." + name, "enabled", true).getBoolean(true);
            
            if (isEnabled) {
                module.enable();
            } else {
                module.disable();
            }
        });

        config.save();
        config.load();
    }

    public static void handleConfigChanged() {
        getInstance()._handleConfigChanged();

        logger.info("Updated config!");
    }

    private static final List<Runnable> initListeners = new ArrayList<Runnable>();
    public static void onInit(Runnable listener) {
        initListeners.add(listener);

        if (hasInitialized) {
            listener.run();
        }
    }
    private static void triggerInitListeners() {
        for (Runnable listener : initListeners) {
            listener.run();
        }
    }

    public static String formatDurationUntil(long time) {
        return formatDuration(time - System.currentTimeMillis());
    }

    public static String formatDuration(long duration) {
        long hours = duration / (60 * 60 * 1000);
        long minutes = (duration / (60 * 1000)) % 60;
        return "" + (hours != 0 ? (hours + "h ") : "") + ((minutes != 0 || hours != 0) ? (minutes + "m ") : "") + ((duration % (60 * 1000)) / 1000) + "s";
    }

    public static Configuration getConfig() {
        return getInstance().config;
    }
}
