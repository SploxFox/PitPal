package pit.splox.pitpal.modules.snooper;

public class SnooperMessage {
    public final String pitServerInstance;
    public final Object content;
    public final long time;

    public SnooperMessage(String pitServerInstance, Object content) {
        this.pitServerInstance = pitServerInstance;
        this.content = content;
        time = System.currentTimeMillis();
    }
}
