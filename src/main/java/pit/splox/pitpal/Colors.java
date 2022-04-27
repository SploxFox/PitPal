package pit.splox.pitpal;

import java.awt.Color;

public class Colors {
    public static int getShadow(int color) {
        float[] hsb = Color.RGBtoHSB((color >> 16) % 255, (color >> 8) % 255, (color >> 0) % 255, null);
        hsb[2] = Math.max(hsb[2] - 0.3f, 0);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }
}
