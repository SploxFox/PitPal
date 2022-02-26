package pit.splox.pitpal.modules;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pit.splox.pitpal.Module;

import static pit.splox.pitpal.PitPal.*;

public class SewerChatPings extends Module {
    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();
        if (msg.startsWith("SEWERS! A new treasure")) {
            mc.thePlayer.playSound("mob.cat.meow", 1, 1.2f);
        } else if (msg.startsWith("SEWERS!")) {
            mc.thePlayer.playSound("mob.cat.meow", 1, 0.5f);
        }
    }
}
