package pit.splox.pitpal.modules;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pit.splox.pitpal.Module;

import static pit.splox.pitpal.PitPal.*;

public class ChatPings extends Module {
    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        if (state.connectedToPit) {
            String msg = event.message.getUnformattedText();
            if (msg.startsWith("[") && msg.contains(mc.thePlayer.getName())) {
                mc.thePlayer.playSound("random.orb", 1, 1.2f);
            }
        }
    }
}

