package eu.ciechanowiec.templater.service;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class UpcomingTimeAndDayCalculator {

    private static final int HOURS_IN_DAY = 24;

    public int[] calculateNextTenHours(LocalTime localTime) {
        int hour = localTime.getHour();

        int[] nextHours = new int[5];
        hour += 2;
        nextHours[0] = hour;

        for (int i = 1; i < nextHours.length; i++) {
            nextHours[i] = (hour + 2 * i) % HOURS_IN_DAY;
        }
        return nextHours;
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
