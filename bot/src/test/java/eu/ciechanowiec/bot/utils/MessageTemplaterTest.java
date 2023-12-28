package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.Location;
import eu.ciechanowiec.bot.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTemplaterTest {

    @Test
    void greetMessageShouldContainName() {
        MessageTemplater messageTemplater = new MessageTemplater();
        String actualMessage = messageTemplater.getGreetMessage("test");

        String expectedMessage = """
                Hello, test! 👋
                Welcome to Weather Mantra Bot. It provides daily weather forecasts for your chosen location and time.
                You can also request the current weather forecast at any moment.
                """;

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testNextReport() {
        MessageTemplater messageTemplater = new MessageTemplater();

        Location.LocationDetails locationDetails =
                new Location.LocationDetails("test", "test", 0.0, 0.0);
        Location location = new Location(locationDetails);

        User user = new User(1L, 0.0, 0.0,
                LocalTime.of(0, 0), "test", "test", true);

        String actualMessage = messageTemplater.getWhenNextReports(location, user);

        String expectedMessage = """
                ⏰ Your daily weather updates are scheduled for *00:00*
                🌍 Location: *test, test*
                """;

        assertEquals(expectedMessage, actualMessage);
    }
}
