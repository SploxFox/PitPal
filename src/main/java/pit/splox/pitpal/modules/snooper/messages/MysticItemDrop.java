package pit.splox.pitpal.modules.snooper.messages;

import pit.splox.pitpal.helpers.PitItemStack;

public class MysticItemDrop {
    public final PitItemStack item;
    public static final String type = "mysticItemDrop";
    public MysticItemDrop(PitItemStack item) {
        this.item = item;
    }
}
