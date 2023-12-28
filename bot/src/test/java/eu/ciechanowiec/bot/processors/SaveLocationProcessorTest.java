package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("h2")
class SaveLocationProcessorTest {

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private UserService userService;
    @Autowired
    private SaveLocationProcessor saveLocationProcessor;
    @Autowired
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<MessageDTO> commandCaptor;
    private static final double UTC1_LONGITUDE = 13.404954;
    private static final double UTC1_LATITUDE = 52.5200;
    private static final double UTC0_LATITUDE = 51.509865;
    private static final double UTC0_LONGITUDE = -0.118092;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void shouldSaveLocation() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).onUpdateReceived(any(MessageDTO.class));
        User testUser = new User(1L, null, null, null,
                "testUserId", "testUserName", false);

        userRepository.save(testUser);
        assertFalse(userRepository.isLocationSpecified(1L));

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Location location = new Location();
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        message.setLocation(location);

        long id = 1L;
        chat.setId(id);
        message.setChat(chat);
        update.setMessage(message);

        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        saveLocationProcessor.process(messageDTO);

        assertAll(
                () -> verify(spyBot, times(1)).onUpdateReceived(commandCaptor.capture()),
                () -> assertEquals(Command.ASK_TIME, commandCaptor.getValue().command()),
                () -> assertTrue(userService.isLocationSpecified(1L))
        );
    }


    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void shouldAdjustTime() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).onUpdateReceived(any(MessageDTO.class));

        User user = new User(1L, UTC1_LONGITUDE, UTC1_LATITUDE, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);
        userRepository.save(user);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Location location = new Location();
        location.setLatitude(UTC0_LATITUDE);
        location.setLongitude(UTC0_LONGITUDE);
        message.setLocation(location);

        long id = 1L;
        chat.setId(id);
        message.setChat(chat);
        update.setMessage(message);

        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        saveLocationProcessor.process(messageDTO);

        UserService mockService = Mockito.mock(UserService.class);
        when(mockService.determinConfigurationStage(anyLong())).thenReturn(ConfigurationStage.NO_LOCATION_AND_TIME);

        User userFromDatabase = userService.findUser(1L);
        LocalTime localTime = userFromDatabase.getTime();

        verify(spyBot, times(1)).onUpdateReceived(commandCaptor.capture());


        assertAll(
                () -> assertEquals(Command.SHOW_CURRENT_SETTINGS, commandCaptor.getValue().command()),
                () -> assertTrue(userService.isLocationSpecified(1L)),
                () -> assertEquals(UTC0_LONGITUDE, userService.findLongitude(1L)),
                () -> assertEquals(UTC0_LATITUDE, userService.findLatitude(1L)),
                () -> assertEquals(localTime, LocalTime.of(1, 0))
        );
    }
    @Test
    void commandShouldBeSaveTime() {
        Command command = saveLocationProcessor.getCommandType();
        assertEquals(Command.SAVE_LOCATION, command);
    }
}
