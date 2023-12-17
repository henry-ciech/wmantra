package eu.ciechanowiec.templater.service;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class ForecastTimeDayGetter {

    private static final int HOURS_IN_DAY = 24;

    public int[] getNextTenHours(LocalTime localTime) {
        int hour = localTime.getHour();

        int[] nextHours = new int[5];
        hour += 2;
        nextHours[0] = hour;

        for (int i = 1; i < nextHours.length; i++) {
            nextHours[i] = (hour + 2 * i) % HOURS_IN_DAY;
        }
        return nextHours;
    }

    public String[] getNextTwoDays(String currentDay) {
        DayOfWeek day = DayOfWeek.valueOf(currentDay.toUpperCase());

        String[] nextDays = new String[2];

        for (int i = 1; i <= 2; i++) {
            DayOfWeek nextDay = day.plus(i);
            nextDays[i - 1] = nextDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }
        return nextDays;
    }
}
