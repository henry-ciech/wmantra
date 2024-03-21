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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@SuppressWarnings("ClassFanOutComplexity")
class ShowCurrentSettingsProcessorTest {

    private static final double UTC0_LATITUDE = 51.509_865;
    private static final double UTC0_LONGITUDE = -0.118_092;
    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private ShowCurrentSettingsProcessor showCurrentSettingsProcessor;
    @Autowired
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void testFullConfigurationMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        User user = new User(1L, UTC0_LONGITUDE, UTC0_LATITUDE, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);

        userRepository.save(user);
        MessageDTO messageDTO = getMessageDTO(user);
        showCurrentSettingsProcessor.process(messageDTO);

        verify(spyBot).execute(sendMessageCaptor.capture());
        SendMessage sendMessage = sendMessageCaptor.getValue();
        String actualText = sendMessage.getText();
        String expectedText = """
                ⏰ Your daily weather updates are scheduled for *00:00*
                🌍 Location: *London, United Kingdom*
                """;
        assertEquals(expectedText, actualText);
    }

    private static MessageDTO getMessageDTO(User user) {
        Chat chat = new Chat();
        chat.setId(user.getChatId());
        Location location = new Location();
        location.setLongitude(user.getLongitude());
        location.setLatitude(user.getLatitude());
        ChatLocation chatLocation = new ChatLocation();
        chatLocation.setLocation(location);
        chat.setLocation(chatLocation);
        chat.setUserName(user.getUserId());
        chat.setFirstName(user.getUserName());
        Message message = new Message();
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        return new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
    }

    @Test
    void commandShouldBeShowCurrentSettings() {
        Command command = showCurrentSettingsProcessor.getCommandType();
        assertEquals(Command.SHOW_CURRENT_SETTINGS, command);
    }
}
