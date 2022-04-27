package pit.splox.pitpal.modules.snooper;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pit.splox.pitpal.AlwaysEnabled;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.PitPal;
import pit.splox.pitpal.helpers.PitItemStack;
import pit.splox.pitpal.modules.snooper.messages.MysticEnchant;
import pit.splox.pitpal.modules.snooper.messages.MysticItemDrop;
import pit.splox.pitpal.pit_splox.PitSploxApi;

import static pit.splox.pitpal.PitPal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

/**
 * Listens to incoming packets, chat & game state
 */
@AlwaysEnabled
@SideOnly(Side.CLIENT)
public class Snooper extends Module {
    private List<SnooperMessage> pendingMessages = new ArrayList<SnooperMessage>();
    private long lastMysticDrop = 0;

    public Snooper() {
        state.onUpdate(s -> {
            if (!s.connectedToPit) {
                sendMessages();
            }
        });

        scheduler.scheduleAtFixedRate(() -> sendMessages(), 0, 60, TimeUnit.SECONDS);
    }

    /**
     * List of ids (e.g. Mystic nonces or entity UUIDs) so that we don't report the same thing twice
     */
    List<String> foundIds = new ArrayList<String>();

    private void addMessage(Object msg) {
        /*if (!state.connectedToPit) {
            return;
        }*/

        pendingMessages.add(new SnooperMessage(state.pitServerInstance, msg));
    }

    public void sendMessages() {
        if (pendingMessages.size() > 0) {
            // Duplicate the array so that when we clear it later we dont clear this one
            List<SnooperMessage> newList = new ArrayList<SnooperMessage>();
            newList.addAll(pendingMessages);
            PitSploxApi.postSnooperMessages(newList);

            logger.info("Sent snooper messages: ");
            pendingMessages.forEach(msg -> logger.info(gson.toJson(msg)));
        }
        
        pendingMessages.clear();
    }

    @SubscribeEvent
    public void handleKey(KeyInputEvent event) {
        // For debug purposes
        if (Keyboard.isKeyDown(Keyboard.KEY_F6)) {
            sendMessages();
        }
    }

    @SubscribeEvent
    public void handleConnect(ClientConnectedToServerEvent event) {
        PitPal.logger.info("Connected to server.");

        //mc.getNetHandler().getNetworkManager().channel().pipeline().addLast();
    }

    private ArrayList<Entity> loadedEntities = new ArrayList<Entity>();

    @SubscribeEvent
    public void handleTick(TickEvent event) {
        /*if (mc.theWorld == null) {
            return;
        }

        // Grab gold positions. Copy to different arraylist to prevent concurrent modification
        loadedEntities.clear();
        loadedEntities.addAll(mc.theWorld.loadedEntityList);

        loadedEntities.stream()
                .filter(entity -> entity instanceof EntityItem)
                .map(entity -> (EntityItem) entity) // Cast to item
                .filter(item -> item.getEntityItem().getItem().getRegistryName().equals("minecraft:gold_ingot"))
                .forEach(item -> logger.info("GOLD ITEM: " + item.posX + " " + item.posY + " " + item.posZ));*/
    }

    @SubscribeEvent
    public void handleChat(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();

        if (msg.startsWith("MYSTIC ITEM! dropped")) {
            lastMysticDrop = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void handleItemPickup(ItemPickupEvent event) {
        if (event.player == mc.thePlayer) {
            PitItemStack item = PitItemStack.from(event.pickedUp.getEntityItem());

            if (System.currentTimeMillis() - lastMysticDrop < 20000 && item.isFresh()) {
                addMessage(new MysticItemDrop(item));
            }
        }
    }

    @SubscribeEvent
    public void handleEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityItem) {
            handleItemSpawn((EntityItem) event.entity);
        }
    }

    private boolean isEnchanting = false;
    private PitItemStack beforeEnchanting = null;
    private PitItemStack afterEnchanting = null;
    private void clearEnchanting() {
        if (isEnchanting) {
            logger.info("Cleared enchanting");
        }

        beforeEnchanting = null;
        afterEnchanting = null;
    }

    private void closeEnchantingGui() {
        if (isEnchanting) {
            logger.info("Closed enchant gui");
        }
        isEnchanting = false;
    }

    @SubscribeEvent
    public void handleGuiScreen(GuiScreenEvent event) {
        if (event.gui instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.gui;

            // Check to see if there is an enchanting table in slot 24,
            // ie basically checking if the mystic well gui is open.
            if (slotIsMysticWell(gui, 24) || slotIsMysticWell(gui, 25)) {
                if (!isEnchanting) {
                    logger.info("Opened enchant gui");
                }
                isEnchanting = true;
            }

            if (isEnchanting && (beforeEnchanting == null || afterEnchanting == null)) {
                ItemStack stack = gui.inventorySlots.getSlot(20).getStack();
                
                if (stack != null) {
                    PitItemStack wellItem = PitItemStack.from(stack);
                    if (wellItem.hasPitAttributes() && wellItem.hasNonce()) {
                        if (beforeEnchanting == null) {
                            beforeEnchanting = wellItem;
                            logger.info("Before enchanting set to " + wellItem.getNonce());
                        } else if (afterEnchanting == null
                                //&& stack != beforeEnchanting.itemStack
                                && !stack.getTagCompound().toString().equals(beforeEnchanting.itemStack.getTagCompound().toString())) {
                            afterEnchanting = wellItem;
                            logger.info("After enchanting set to " + wellItem.getNonce());

                            // Ensure it's actually the same item
                            if (afterEnchanting.getNonce() == beforeEnchanting.getNonce()) {
                                logger.info("Same nonce: " + beforeEnchanting.getNonce() + " -- done!");
                                addMessage(new MysticEnchant(beforeEnchanting.itemStack.getTagCompound().toString(), afterEnchanting.itemStack.getTagCompound().toString(), beforeEnchanting.getItemId()));
                            } else {
                                logger.info("Different nonce, ie different item: Nonces: " + beforeEnchanting.getNonce() + " and " + afterEnchanting.getNonce());
                            }

                            clearEnchanting();
                        }
                    } else {
                        //logger.debug("Well item not pit item");
                    }
                } else {
                    //logger.debug("Null stack");
                }
            }
        }
    }

    private boolean slotIsMysticWell(GuiContainer gui, int slot) {
        return gui.inventorySlots.getSlot(slot) != null 
                    && gui.inventorySlots.getSlot(slot).getStack() != null
                    && Item.getIdFromItem(gui.inventorySlots.getSlot(slot).getStack().getItem()) == 116
                    && gui.inventorySlots.getSlot(slot).getStack().getDisplayName().contains("Mystic Well");
    }

    @SubscribeEvent
    public void handleGuiScreenOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            // If it's null, we're actually closing a gui.
            closeEnchantingGui();
        }
    }

    public void handleItemSpawn(EntityItem item) {

    }
}
