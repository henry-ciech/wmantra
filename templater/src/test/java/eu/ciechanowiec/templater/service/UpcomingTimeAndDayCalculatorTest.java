package eu.ciechanowiec.templater.service;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class UpcomingTimeAndDayCalculatorTest {

    @SuppressWarnings("MagicNumber")
    @Test
    void testCalculateNextTenHours() {
        UpcomingTimeAndDayCalculator calculator = new UpcomingTimeAndDayCalculator();

        int[] actualFirstTest = calculator.calculateNextTenHours(LocalTime.of(8, 0));
        int[] actualSecondTest = calculator.calculateNextTenHours(LocalTime.of(20, 0));
        int[] actualThirdTest = calculator.calculateNextTenHours(LocalTime.of(23, 30));

        assertAll(
                () -> assertArrayEquals(new int[]{10, 12, 14, 16, 18}, actualFirstTest),
                () -> assertArrayEquals(new int[]{22, 0, 2, 4, 6}, actualSecondTest),
                () -> assertArrayEquals(new int[]{2, 4, 6, 8, 10}, actualThirdTest)
        );
    }

    @Test
    void testCalculateNextTwoDays() {
        UpcomingTimeAndDayCalculator calculator = new UpcomingTimeAndDayCalculator();

        String[] actualFirstTest = calculator.calculateNextTwoDays(DayOfWeek.MONDAY);
        String[] actualSecondTest = calculator.calculateNextTwoDays(DayOfWeek.FRIDAY);
        String[] actualThirdTest = calculator.calculateNextTwoDays(DayOfWeek.SUNDAY);

        assertAll(
                () -> assertArrayEquals(new String[]{"Tuesday", "Wednesday"}, actualFirstTest),
                () -> assertArrayEquals(new String[]{"Saturday", "Sunday"}, actualSecondTest),
                () -> assertArrayEquals(new String[]{"Monday", "Tuesday"}, actualThirdTest)
        );
    }
}
