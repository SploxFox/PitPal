package pit.splox.pitpal.day_night;

public enum DayOrNight {
    DAY("Day"),
    NIGHT("Night");

    public final String name;
    DayOrNight(String name) {
        this.name = name;
    }
}