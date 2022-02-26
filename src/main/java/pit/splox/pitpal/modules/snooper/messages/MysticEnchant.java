package pit.splox.pitpal.modules.snooper.messages;

public class MysticEnchant {
    public final String before;
    public final String after;
    public final int itemId;
    public final String type = "mysticEnchant";
    public MysticEnchant(String before, String after, int itemId) {
        this.before = before;
        this.after = after;
        this.itemId = itemId;
    }
}
