package pit.splox.pitpal.pit_events;

public class PitEvent {
    public final long start;
    public final String name;
    public final String type;
    public PitEvent(long start, String name, String type) {
        this.start = start;
        this.name = name;
        this.type = type;
    }
}
