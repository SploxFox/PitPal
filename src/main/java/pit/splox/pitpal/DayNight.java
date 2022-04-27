package pit.splox.pitpal;

enum DayOrNight {
    DAY("Day"),
    NIGHT("Night");

    public final String name;
    DayOrNight(String name) {
        this.name = name;
    }
}

public class DayNight {
    private DayOrNight dayOrNight;
    private long endTime;

    private static long nightLength = 12l * 60l * 1000l;
    private static long dayLength = nightLength * 2l;
    private static DayNight dayNight = new DayNight();
    private static DayNight getDayNight() {
        long currentTime = System.currentTimeMillis();
        
        long nightStartEpoch = 1622671200000l - (10_000l);
        long cycleLength = nightLength + dayLength;

        long cycleTime = (currentTime - nightStartEpoch) % cycleLength;
        long nextNightfall = ((currentTime - nightStartEpoch) / cycleLength + 1) * cycleLength + nightStartEpoch;
        
        if (cycleTime < nightLength) {
            // Currently night.
            dayNight.endTime = nextNightfall - dayLength;
            dayNight.dayOrNight = DayOrNight.NIGHT;
        } else {
            // Currently day.
            dayNight.endTime = nextNightfall;
            dayNight.dayOrNight = DayOrNight.DAY;
        }

        return dayNight;
    }

    public static DayOrNight getDayOrNight() {
        return getDayNight().dayOrNight;
    }

    public static long getEndTime() {
        return getDayNight().endTime;
    }

    public static double getProgress() {
        DayNight dayNight = getDayNight();
        
        long length;
        if (dayNight.dayOrNight == DayOrNight.DAY) {
            length = dayLength;
        } else {
            length = nightLength;
        }

        return ((double)(dayNight.endTime - System.currentTimeMillis()))/((double) length);
    }
}
