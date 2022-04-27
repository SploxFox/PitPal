package pit.splox.pitpal.pit_state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.entity.player.InventoryPlayer;
import pit.splox.pitpal.Listenable;
import pit.splox.pitpal.PitPal;

public class PitState extends Listenable<PitState> {
    public boolean connectedToPit = false;
    public String pitServerInstance = "not connected";
    public boolean treasureSpawned = false;
    public PitInventory inventory = new PitInventory();
    public List<String> scoreboard = new ArrayList<String>();
    public double goldSpentOnItemsThisLife = 0;
    public double goldEarnedThisLife = 0;
    public int xpEarnedThisLife = 0;

    public static void updateState(Consumer<PitState> updater) {
        PitPal.state.update(updater);
    }
}
