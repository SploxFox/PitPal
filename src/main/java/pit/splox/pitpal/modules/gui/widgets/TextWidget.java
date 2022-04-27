package pit.splox.pitpal.modules.gui.widgets;

import static pit.splox.pitpal.PitPal.*;

public class TextWidget extends Widget {
    public final String text;
    public final int color;
    public TextWidget(String text, int color) {
        this.text = text;
        this.color = color;
    }

    public int getWidth() {
        return getFontRenderer().getStringWidth(text);
    }

    public int getHeight() {
        return getFontRenderer().FONT_HEIGHT;
    }

    public void draw(int x, int y) {
        getFontRenderer().drawStringWithShadow(text, x, y, color);
    }
}
