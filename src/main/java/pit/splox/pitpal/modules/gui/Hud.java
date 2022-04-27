package pit.splox.pitpal.modules.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentStyle;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ChatColorEntry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import pit.splox.pitpal.Colors;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.PitPal;
import pit.splox.pitpal.Module;
import pit.splox.pitpal.day_night.DayNight;
import pit.splox.pitpal.day_night.DayOrNight;
import pit.splox.pitpal.modules.gui.widgets.TextWidget;
import pit.splox.pitpal.modules.gui.widgets.Widget;
import pit.splox.pitpal.pit_events.PitEvent;
import pit.splox.pitpal.pit_splox.PitSploxApi;
import pit.splox.pitpal.pit_state.PitState;

import static pit.splox.pitpal.PitPal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.input.Keyboard;

public class Hud extends Module {
    private static CompletableFuture<List<PitEvent>> pitEvents = PitSploxApi.fetchEvents();
    private List<Widget> widgets = new ArrayList<Widget>();
    private boolean isVisible = true;
    private ScaledResolution res;
    public Hud() {

        widgets.add(new TextWidget("Mystics equipped", 0xffaaaa){
            public boolean isVisible() {
                return PitPal.getInstance().state.inventory.hasPerishableMystics();
            }
        } );
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
            isVisible = isVisible ? false : true;
        }
    }

    @SubscribeEvent
    public void handleRender(RenderGameOverlayEvent.Post event) {
        if (isVisible && event.type == ElementType.ALL) {
            res = event.resolution;
            // Adding this line before the following code fixes an issue where the hotbar changes color
            // idk why this works but it does
            Gui.drawRect(0, 0, 0, 0, 0xffFFffFF);

            GlStateManager.pushAttrib();

            // Day/night stuff
            DayOrNight dayOrNight = DayNight.getDayOrNight();
            String text = dayOrNight.name + " " + formatDurationUntil(DayNight.getEndTime());
            drawStatusBar(10, 10, text, DayNight.getProgress(), dayOrNight == DayOrNight.DAY ? 0xff9c5209 : 0xff3022ab);

            // Other stuff
            drawEvents();
            drawWidgets();

            GlStateManager.popAttrib();
        }
    }

    private void drawWidgets() {
        widgets.removeIf(w -> !w.shouldStay());

        int padding = 10;
        int height = padding;
        for (Widget widget : widgets) {
            if (!widget.isVisible()) {
                continue;
            }

            widget.drawRight(res.getScaledWidth() - padding, height);
            height += widget.getHeight() + padding;
        }
    }

    private void drawStatusBar(int x, int y, String text, double progress, int color) {
        int bg = Colors.getShadow(color);
        int width = 80;
        Gui.drawRect(x - 2, y - 2, x + width + 4, y + mc.fontRendererObj.FONT_HEIGHT + 3, bg);
        Gui.drawRect(x - 1, y - 1, x + (int)((width + 2) * progress), y + mc.fontRendererObj.FONT_HEIGHT + 2, color);
        mc.fontRendererObj.drawString(text, x + 1, y + 1, 0xffffff, false);
    }

    public void drawEvents() {
        List<PitEvent> pitEventsList = pitEvents.getNow(new ArrayList<PitEvent>());

        int i = 0;
        double scale = 0.8;
        int startY = 31;
        for (PitEvent event : pitEventsList) {
            int x = 11;
            int y = startY + (i * 20);

            if (i >= 7) {
                break;
            } else if (event.start < System.currentTimeMillis()) {
                continue;
            }
            mc.fontRendererObj.drawString((event.type.equals("major") ? "\u00a7l" : "") + event.name, x, y, (event.type.equals("major") ? 0xbb62ff : 0xffffff), true);
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1);
            mc.fontRendererObj.drawString(formatDurationUntil(event.start), (int) (x * (1/scale)), (int) ((y + 10) * (1/scale)), 0xaaaaaa, true);
            GlStateManager.popMatrix();
            i++;
        }
    }

    public void drawCountdown(int x, int y, String text, long endTime, long duration, int color) {

    }
}
