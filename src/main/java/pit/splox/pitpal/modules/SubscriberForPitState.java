package pit.splox.pitpal.modules;

import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import pit.splox.pitpal.AlwaysEnabled;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.PitPal;
import pit.splox.pitpal.pit_state.PitInventory;

import static pit.splox.pitpal.PitPal.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AlwaysEnabled
public class SubscriberForPitState extends Module {
    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();
        if (msg.startsWith("SEWERS! A new treasure")) {
            state.update(s -> s.treasureSpawned = true);
        } else if (msg.startsWith("SEWERS!")) {
            state.update(s -> s.treasureSpawned = false);
        }
    }

    @SubscribeEvent
    public void handleTick(TickEvent event) {

        if (event.phase == Phase.END
                && event.type == TickEvent.Type.CLIENT) {

            
            if (mc.thePlayer != null) {

                // Update inventory
                {
                    Stream<ItemStack> mainInventory = Arrays.stream(mc.thePlayer.inventory.mainInventory);
                    Stream<ItemStack> armorInventory = Arrays.stream(mc.thePlayer.inventory.armorInventory);
                    ItemStack[] combined = Stream.concat(mainInventory, armorInventory).toArray(ItemStack[]::new);

                    boolean isEqual = true;
                    for (int i = 0; i < state.inventory.size; i++) {
                        if (!ItemStack.areItemStacksEqual(state.inventory.getItem(i), combined[i])) {
                            isEqual = false;
                            break;
                        }
                    }

                    if (!isEqual) {
                        state.update(s -> s.inventory = new PitInventory(combined));
                    }
                }

                // Parse scoreboard
                {
                    Scoreboard sb = mc.thePlayer.getWorldScoreboard();
                    ScoreObjective objective = sb.getObjectiveInDisplaySlot(1);
                    Collection<Score> scores = sb.getSortedScores(objective);

                    if (objective != null && objective.getDisplayName() != null && !scores.isEmpty()) {
                        List<String> scoreboardLines = scores.stream()
                            .skip(Math.max(0, 15 - scores.size()))
                            .map(score -> score.getPlayerName())
                            .collect(Collectors.toList());

                        if (scoreboardLines.size() > 0) {
                            state.update(s -> {
                                s.scoreboard = scoreboardLines;
                                s.connectedToPit = objective.getDisplayName().contains("THE HYPIXEL PIT");
                                String[] firstLineParts = scoreboardLines.get(0).split(" ");
                                if (firstLineParts.length > 1) {
                                    s.pitServerInstance = firstLineParts[1];
                                }
                            });
                        }
                    } else {
                        state.update(s -> {
                            s.scoreboard.clear();
                            s.connectedToPit = false;
                        });
                    }
                }
            }
        }
    }
}
