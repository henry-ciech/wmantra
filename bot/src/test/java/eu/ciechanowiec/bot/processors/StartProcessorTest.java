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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
class StartProcessorTest {

    @Captor
    ArgumentCaptor<MessageDTO> commandCaptor;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private StartProcessor startProcessor;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @Test
    void startShouldCreateEmptyUser() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        long id = 1L;
        chat.setId(id);
        String testName = "testUserName";
        chat.setUserName(testName);
        chat.setFirstName(testName);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        startProcessor.process(messageDTO);

        User userFromDatabase = userRepository.findUser(id);

        String userName = userFromDatabase.getUserName();

        verify(spyBot, times(3)).onUpdateReceived(commandCaptor.capture());
        MessageDTO actualMessageDTO = commandCaptor.getAllValues().get(0);

        assertEquals(Command.ASK_LOCATION, actualMessageDTO.command());
        assertEquals(testName, userName);
        assertEquals(testName, userFromDatabase.getUserId());
        assertEquals(id, userFromDatabase.getChatId());
    }

    @SneakyThrows
    @Test
    void startShouldShowCurrentSettings() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));

        User testUser = new User(1L, 0.0, 0.0, null,
                "testUserName", "testUserName", false);
        userRepository.save(testUser);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        long id = 1L;
        chat.setId(id);
        String testName = "testUserName";
        chat.setUserName(testName);
        chat.setFirstName(testName);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        startProcessor.process(messageDTO);

        verify(spyBot, times(4)).onUpdateReceived(commandCaptor.capture());
        List<MessageDTO> actualMessageDTO = commandCaptor.getAllValues();

        assertEquals(Command.SHOW_CURRENT_SETTINGS, actualMessageDTO.get(1).command());
        assertEquals(Command.ASK_TIME, actualMessageDTO.get(0).command());
    }


    @Test
    void commandShouldBeStart() {
        Command command = startProcessor.getCommandType();
        assertEquals(Command.START, command);
    }
}
