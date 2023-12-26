package eu.ciechanowiec.bot.model;

import eu.ciechanowiec.bot.utils.MessageTemplater;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
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
        message.setLocation(new Location());

        Command result = Command.of(message);
        assertEquals(Command.SAVE_LOCATION, result);
    }

    @Test
    void messageWithValidTimeShouldReturnSaveTime() {
        Message message = new Message();
        message.setText("00:00");

        Command result = Command.of(message);
        assertEquals(Command.SAVE_TIME, result);
    }

    @ParameterizedTest
    @MethodSource("args")
    void messageTextCorrespondingToTheCommandNameShouldReturnThisCommand(Command expectedCommand, String text) {
        Message message = new Message();
        message.setText(text);

        Command result = Command.of(message);
        assertEquals(expectedCommand, result);
    }

    static Stream<Arguments> args() {
       return Stream.of(
               arguments(Command.START, "/start"),
               arguments(Command.UNKNOWN, "/askLocation"),
               arguments(Command.UNKNOWN, "/askTime"),
               arguments(Command.CONFIG, "/config"),
               arguments(Command.CONFIG, MessageTemplater.CONFIGURE_BUTTON_TEXT),
               arguments(Command.UNKNOWN, "/unknown"),
               arguments(Command.SHOW_CURRENT_SETTINGS, "/showCurrentSettings"),
               arguments(Command.SHOW_CURRENT_WEATHER, "/showCurrentWeather"),
               arguments(Command.SHOW_CURRENT_WEATHER, MessageTemplater.SHOW_CURRENT_SETTINGS_BUTTON_TEXT),
               arguments(Command.UNKNOWN, "/default"),
               arguments(Command.UNKNOWN, "/saveLocation"),
               arguments(Command.UNKNOWN, "/saveTime")
       );
    }

    @Test
    void messageWithInvalidTextShouldReturnUnknown() {
        Message message = new Message();
        message.setText("test");

        Command result = Command.of(message);
        assertEquals(Command.UNKNOWN, result);
    }
}
