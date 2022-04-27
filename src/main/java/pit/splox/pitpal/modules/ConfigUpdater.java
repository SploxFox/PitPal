package pit.splox.pitpal.modules;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import pit.splox.pitpal.AlwaysEnabled;
import pit.splox.pitpal.Module;

import static pit.splox.pitpal.PitPal.*;

@AlwaysEnabled
public class ConfigUpdater extends Module {
    @SubscribeEvent
    public void handleTick(TickEvent event) {
        if (event.phase == Phase.END && event.side == Side.CLIENT) {
            if (getGlobalConfig().hasChanged()) {
                handleConfigChanged();
            }
        }
    }
}
