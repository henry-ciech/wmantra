package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@ActiveProfiles("h2")
class SaveTimeProcessorTest {

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private UserService userService;
    @Autowired
    private SaveTimeProcessor saveTimeProcessor;
    @Autowired
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<MessageDTO> commandCaptor;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void shouldSaveTime() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).onUpdateReceived(any(MessageDTO.class));

        User testUser = new User(1L, 0.0, 0.0, null,
                "testUserId", "testUserName", false);

        userRepository.save(testUser);
        Chat chat = new Chat();
        long id = 1L;
        chat.setId(id);
        String testName = "testName";
        chat.setUserName(testName);
        chat.setFirstName(testName);
        Message message = new Message();
        message.setChat(chat);
        message.setText("0:00");
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);

        saveTimeProcessor.process(messageDTO);

        assertAll(
                () -> verify(spyBot, times(1)).onUpdateReceived(commandCaptor.capture()),
                () -> assertEquals(Command.SHOW_CURRENT_SETTINGS, commandCaptor.getValue().command()),
                () -> assertTrue(userService.isTimeSpecified(1L))
        );
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void shouldRedirectToAskLocation() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).onUpdateReceived(any(MessageDTO.class));

        Chat chat = new Chat();
        long id = 1L;
        chat.setId(id);
        Message message = new Message();
        message.setChat(chat);
        message.setText("8:00");
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.START);
        saveTimeProcessor.process(messageDTO);

        assertAll(
                () -> verify(spyBot).onUpdateReceived(commandCaptor.capture()),
                () -> assertEquals(Command.SHOW_CURRENT_SETTINGS, commandCaptor.getValue().command())
        );
    }

    @Test
    void commandShouldBeSaveTime() {
        Command command = saveTimeProcessor.getCommandType();
        assertEquals(Command.SAVE_TIME, command);
    }
}
