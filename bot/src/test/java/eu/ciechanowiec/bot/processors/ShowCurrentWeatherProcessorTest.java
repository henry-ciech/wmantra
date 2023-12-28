package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import eu.ciechanowiec.bot.service.ImageSender;
import eu.ciechanowiec.bot.service.ScreenshoterClient;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@ActiveProfiles("h2")
class ShowCurrentWeatherProcessorTest {

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private UserRepository userRepository;
    private static final double UTC0_LATITUDE = 51.509865;
    private static final double UTC0_LONGITUDE = -0.118092;
    @Value("${test.image.path}")
    private String testImagePath;
    @Value("${error.image.path}")
    private String errorImagePath;
    @Captor
    private ArgumentCaptor<MessageDTO> messageDTOCaptor;

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void redirectToShowCurrentSettings() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);
        ImageSender imageSender = getImageSenderSpy(mockScreenshoterClient);
        UserService userService = Mockito.mock(UserService.class);
        when(userService.determinConfigurationStage(anyLong())).thenReturn(ConfigurationStage.NO_LOCATION_AND_TIME);

        Processor showCurrentWeatherProcessor = new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
        showCurrentWeatherProcessor.process(messageDTO);
        verify(spyBot, times(1)).onUpdateReceived(messageDTOCaptor.capture());
        Command command = messageDTOCaptor.getValue().command();

        assertEquals(Command.SHOW_CURRENT_SETTINGS, command);
    }

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void shouldSendImage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendChatAction.class));
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendPhoto.class));

        UserService userService = new UserService(userRepository);

        Path path = Paths.get(testImagePath);
        byte[] testImageBytes = Files.readAllBytes(path);

        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);
        when(mockScreenshoterClient.getImageAsInputStream(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any()))
                .thenReturn(new ByteArrayInputStream(testImageBytes));
        ImageSender imageSender = getImageSenderSpy(mockScreenshoterClient);
        ReflectionTestUtils.setField(imageSender, "imagePath", testImagePath);

        Processor showCurrentWeatherProcessor = new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);
        User user = new User(1L, UTC0_LONGITUDE, UTC0_LATITUDE, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);

        ReflectionTestUtils.setField(imageSender, "imagePath", errorImagePath);

        userRepository.save(user);

        MessageDTO messageDTO = getMessageDTO(user);
        showCurrentWeatherProcessor.process(messageDTO);

        verify(imageSender).sendImageToTheUser(anyLong());
        Command command = showCurrentWeatherProcessor.getCommandType();
        assertEquals(Command.SHOW_CURRENT_WEATHER, command);
    }

    private static MessageDTO getMessageDTO(User user) {
        Update update = new Update();
        Message message = new Message();
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
        message.setChat(chat);
        update.setMessage(message);
        return new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
    }

    ImageSender getImageSenderSpy(ScreenshoterClient screenshoterClient) {
        UserService userService = new UserService(userRepository);
        ImageSender imageSender = new ImageSender(userService, screenshoterClient, spyBot);
        return Mockito.spy(imageSender);
    }
}
