package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SaveLocationProcessorTest {

    @Autowired
    TelegramBot spyBot;
    @Autowired
    UserService userService;
    @Autowired
    SaveLocationProcessor saveLocationProcessor;
    @Autowired
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<MessageDTO> commandCaptor;

    @SneakyThrows
    @Test
    void shouldSaveLocation() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).onUpdateReceived(any(MessageDTO.class));

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

        verify(spyBot, times(2)).onUpdateReceived(commandCaptor.capture());

        assertEquals(Command.ASK_TIME, commandCaptor.getAllValues().get(1).command());

        assertTrue(userService.isLocationSpecified(1L));
    }


    @SneakyThrows
    @Test
    void shouldAdjustTime() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).onUpdateReceived(any(MessageDTO.class));

        User user = new User(1L, 13.404954, 52.5200, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);
        userRepository.save(user);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Location location = new Location();
        location.setLatitude(51.509865);
        location.setLongitude(-0.118092);
        message.setLocation(location);

        long id = 1L;
        chat.setId(id);
        message.setChat(chat);
        update.setMessage(message);

        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        saveLocationProcessor.process(messageDTO);

        UserService mockService = Mockito.mock(UserService.class);
        when(mockService.determinConfigurationStage(anyLong())).thenReturn(ConfigurationStage.NO_LOCATION_AND_TIME);

        verify(spyBot, times(3)).onUpdateReceived(commandCaptor.capture());

        assertEquals(Command.SHOW_CURRENT_SETTINGS, commandCaptor.getAllValues().get(2).command());

        assertTrue(userService.isLocationSpecified(1L));
        assertEquals(userService.findLongitude(1L), -0.118092);
        assertEquals(userService.findLatitude(1L), 51.509865);
        User userFromDatabase = userService.findUser(1L);
        LocalTime localTime = userFromDatabase.getTime();
        assertEquals(localTime, LocalTime.of(1, 0));

    }


    @Test
    void commandShouldBeSaveTime() {
        Command command = saveLocationProcessor.getCommandType();
        assertEquals(Command.SAVE_LOCATION, command);
    }
}
