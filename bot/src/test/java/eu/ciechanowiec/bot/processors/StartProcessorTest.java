package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("h2")
class StartProcessorTest {

    private static final String TEST_USER_NAME = "testUserName";
    @Captor
    private ArgumentCaptor<MessageDTO> commandCaptor;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private StartProcessor startProcessor;

    @AfterEach
    void resetSpy() {
        reset(spyBot);
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void startShouldCreateEmptyUser() {
        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        Chat chat = new Chat();
        long id = 2L;
        chat.setId(id);
        chat.setUserName(TEST_USER_NAME);
        chat.setFirstName(TEST_USER_NAME);
        Message message = new Message();
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        startProcessor.process(messageDTO);

        User userFromDatabase = userRepository.findUser(id);

        String userName = userFromDatabase.getUserName();

        verify(spyBot, times(1)).onUpdateReceived(commandCaptor.capture());
        MessageDTO actualMessageDTO = commandCaptor.getValue();

        assertAll(
                () -> assertEquals(Command.ASK_LOCATION, actualMessageDTO.command()),
                () -> assertEquals(TEST_USER_NAME, userName),
                () -> assertEquals(TEST_USER_NAME, userFromDatabase.getUserId()),
                () -> assertEquals(id, userFromDatabase.getChatId())
        );
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void startShouldShowCurrentSettings() {
        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        User testUser = new User(1L, 0.0, 0.0, null,
                TEST_USER_NAME, TEST_USER_NAME, false);
        userRepository.save(testUser);

        Chat chat = new Chat();
        long id = 1L;
        chat.setId(id);
        chat.setUserName(TEST_USER_NAME);
        chat.setFirstName(TEST_USER_NAME);
        Message message = new Message();
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        startProcessor.process(messageDTO);

        verify(spyBot, times(2)).onUpdateReceived(commandCaptor.capture());
        List<MessageDTO> actualMessageDTO = commandCaptor.getAllValues();

        assertEquals(Command.SHOW_CURRENT_SETTINGS, actualMessageDTO.get(0).command());
    }

    @Test
    void commandShouldBeStart() {
        Command command = startProcessor.getCommandType();
        assertEquals(Command.START, command);
    }
}
