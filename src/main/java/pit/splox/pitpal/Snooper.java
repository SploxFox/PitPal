package pit.splox.pitpal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

/**
 * Listens to incoming packets -- NOT IMPLEMENTED YET
 */
public class Snooper {
    public void init() {
        //MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void handleConnect(ClientConnectedToServerEvent event) {
        PitPal.logger.info("Connected to server.");
    }

    @SubscribeEvent
    public void handleTick(TickEvent event) {
        
    }
}
