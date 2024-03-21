package eu.ciechanowiec.bot.model;

import eu.ciechanowiec.bot.utils.MessageTemplater;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class CommandTest {

    @Test
    void messageWithLocationShouldReturnSaveLocation() {
        Message message = new Message();
        Location location = new Location();
        message.setLocation(location);

        Command actualResult = Command.getCommandFromMessage(message);
        assertEquals(Command.SAVE_LOCATION, actualResult);
    }

    @Test
    void messageWithTimeShouldReturnSaveTime() {
        Message message = new Message();
        message.setText("00:00");

        Command actualResult = Command.getCommandFromMessage(message);
        assertEquals(Command.SAVE_TIME, actualResult);
    }

    @ParameterizedTest
    @MethodSource("args")
    void messageReturnCorrespondingCommand(Command expectedCommand, String text) {
        Message message = new Message();
        message.setText(text);

        Command actualResult = Command.getCommandFromMessage(message);
        assertEquals(expectedCommand, actualResult);
    }

    static Stream<Arguments> args() {
        return Stream.of(
                arguments(Command.START, "/start"),
                arguments(Command.UNKNOWN, "/askLocation"),
                arguments(Command.UNKNOWN, "/askTime"),
                arguments(Command.CONFIG, "/config"),
                arguments(Command.CONFIG, MessageTemplater.CONFIGURE_TEXT),
                arguments(Command.UNKNOWN, "/unknown"),
                arguments(Command.SHOW_CURRENT_SETTINGS, "/showCurrentSettings"),
                arguments(Command.SHOW_CURRENT_WEATHER, "/showCurrentWeather"),
                arguments(Command.SHOW_CURRENT_WEATHER, MessageTemplater.SHOW_CURRENT_WEATHER_TEXT),
                arguments(Command.UNKNOWN, "/default"),
                arguments(Command.UNKNOWN, "/saveLocation"),
                arguments(Command.UNKNOWN, "/saveTime")
        );
    }

    @Test
    void messageWithInvalidTextShouldReturnUnknown() {
        Message message = new Message();
        message.setText("test");

        Command result = Command.getCommandFromMessage(message);
        assertEquals(Command.UNKNOWN, result);
    }
}
