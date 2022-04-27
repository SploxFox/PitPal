package pit.splox.pitpal;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = PitPal.MODID, version = PitPal.VERSION, name = PitPal.NAME, clientSideOnly = true)
public class PitPal {
    /**
     * Info about the mod.
     */
    public static final String MODID = "PitPal";
    public static final String NAME = "PitPal";
    public static final String VERSION = "1.0";

    // Global constants and utils
    /**
     * PitPal logger
     */
    public static final Logger logger = LogManager.getLogger(PitPal.MODID);
    public static final Minecraft mc = Minecraft.getMinecraft();
    
    public static final GsonBuilder gsonBuilder = new GsonBuilder();
    public static final Gson gson = gsonBuilder.create();
    public static final JsonParser jsonParser = new JsonParser();

    // Actual mod stuff
    public final Snooper snooper = new Snooper();
    public final Hud hud = new Hud();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        snooper.init();
        triggerInitListeners();
        logger.info("Initialized pitpal.");
        
        hasInitialized = true;
    }

    private static boolean hasInitialized = false;

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

    public PitPal() {
        pitPalInstance = this;
    }
    private static PitPal pitPalInstance;
    public static PitPal getInstance() {
        return pitPalInstance;
    }

    public static String formatDurationUntil(long time) {
        return formatDuration(time - System.currentTimeMillis());
    }

    public static String formatDuration(long duration) {
        long hours = duration / (60 * 60 * 1000);
        long minutes = (duration / (60 * 1000)) % 60;
        return "" + (hours != 0 ? (hours + "h ") : "") + ((minutes != 0 || hours != 0) ? (minutes + "m ") : "") + ((duration % (60 * 1000)) / 1000) + "s";
    }
}
