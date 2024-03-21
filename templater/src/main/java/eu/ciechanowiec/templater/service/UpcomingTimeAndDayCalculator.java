package eu.ciechanowiec.templater.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class UpcomingTimeAndDayCalculator {

    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_TO_ROUNDUP = 30;

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    public int[] calculateNextTenHours(LocalTime localTime) {
        int hour = localTime.getHour();
        int minutes = localTime.getMinute();
        hour = roundToNextHourIfNecessary(hour, minutes);

        int[] nextHours = new int[5];
        for (int i = 0; i < nextHours.length; i++) {
            nextHours[i] = calculateNextHour(hour, i);
        }
        return nextHours;
    }

    private int roundToNextHourIfNecessary(int hour, int minutes) {
        if (minutes >= MINUTES_TO_ROUNDUP) {
            return (hour + 1) % HOURS_IN_DAY;
        }
        return hour;
    }

    private int calculateNextHour(int startHour, int increment) {
        return (startHour + 2 * (increment + 1)) % HOURS_IN_DAY;
    }

    public String[] calculateNextTwoDays(DayOfWeek day) {

        String[] nextDays = new String[2];

        for (int i = 1; i <= 2; i++) {
            DayOfWeek nextDay = day.plus(i);
            nextDays[i - 1] = nextDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }
        return nextDays;
    }
}
