package pit.splox.pitpal.modules.gui.widgets;

public abstract class Widget {
    /**
     * Method that draws the widget onto the screen
     */
    public abstract void draw(int x, int y);

    public abstract int getWidth();
    public abstract int getHeight();

    public void drawRight(int xRight, int y) {
        draw(xRight - getWidth(), y);
    }

    public boolean shouldStay() {
        return true;
    }

    public boolean isVisible() {
        return true;
    }
}
