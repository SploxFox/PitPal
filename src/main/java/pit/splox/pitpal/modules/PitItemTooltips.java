package pit.splox.pitpal.modules;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.helpers.PitItemStack;

import static pit.splox.pitpal.PitPal.*;

import com.mojang.realmsclient.gui.ChatFormatting;

public class PitItemTooltips extends Module {
    @SubscribeEvent
    public void handleTooltip(ItemTooltipEvent event) {
        PitItemStack pitItemStack = PitItemStack.from(event.itemStack);

        if (pitItemStack.hasNonce() 
                && pitItemStack.getNonce() > 20
                && pitItemStack.getUpgradeTier() < 3) {
            event.toolTip.add("");
            event.toolTip.add("Nonce: " + ChatFormatting.GOLD + pitItemStack.getNonce());
            if (!pitItemStack.isPants()) {
                event.toolTip.add("Requires " + getPantsColorText(pitItemStack.getNonce() % 5) + " pants" + ChatFormatting.GRAY + " to tier III");
            }
        }
    }

    private String getPantsColorText(int color) {
        if (color == 0) {
            return ChatFormatting.RED + "red";
        } else if (color == 1) {
            return ChatFormatting.YELLOW + "yellow";
        } else if (color == 2) {
            return ChatFormatting.BLUE + "blue";
        } else if (color == 3) {
            return ChatFormatting.GREEN + "orange";
        } else {
            return "uh oh";
        }

    }
}
