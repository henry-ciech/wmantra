
package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import eu.ciechanowiec.bot.service.ImageSender;
import eu.ciechanowiec.bot.service.ScreenshotterClient;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("h2")
@SuppressWarnings({"PMD.ExcessiveImports", "ClassDataAbstractionCoupling", "ClassFanOutComplexity"})
class ShowCurrentWeatherProcessorTest {

    private static final double UTC0_LATITUDE = 51_509_865;
    private static final double UTC0_LONGITUDE = -118_092;
    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private UserRepository userRepository;
    @Value("${test.image.path}")
    private String testImagePath;
    @Value("${error.image.path}")
    private String errorImagePath;
    @Captor
    private ArgumentCaptor<MessageDTO> messageDTOCaptor;

    @SneakyThrows
    @Test
    void redirectToShowCurrentSettings() {
        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        UserService userService = mock(UserService.class);
        when(userService.resolveConfigStage(anyLong())).thenReturn(ConfigurationStage.NO_LOCATION_AND_TIME);

        Chat chat = new Chat();
        chat.setId(1L);
        Message message = new Message();
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        ScreenshotterClient mockScreenshotterClient = mock(ScreenshotterClient.class);
        ImageSender imageSender = getImageSenderSpy(mockScreenshotterClient);
        MessageDTO messageDTO = new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
        Processor showCurrentWeatherProcessor = new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);
        showCurrentWeatherProcessor.process(messageDTO);
        verify(spyBot, times(1)).onUpdateReceived(messageDTOCaptor.capture());
        Command command = messageDTOCaptor.getValue().command();

        assertEquals(Command.SHOW_CURRENT_SETTINGS, command);
    }

    @AfterEach
    void resetSpy() {
        reset(spyBot);
    }

//    @SneakyThrows
//    @Test
//    void shouldSendImage() {
//        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
//        doAnswer(invocation -> null).when(spyBot).execute(any(SendChatAction.class));
//        doAnswer(invocation -> null).when(spyBot).execute(any(SendPhoto.class));
//
//        UserService userService = new UserService(userRepository);
//
//        Path path = Paths.get(testImagePath);
//        byte[] testImageBytes = Files.readAllBytes(path);
//
//        ScreenshotterClient mockScreenshotterClient = mock(ScreenshotterClient.class);
//        when(mockScreenshotterClient.getImageAsInputStream(anyDouble(), anyDouble()))
//                .thenReturn(new ByteArrayInputStream(testImageBytes));
//        ImageSender imageSender = getImageSenderSpy(mockScreenshotterClient);
//        ReflectionTestUtils.setField(imageSender, "imagePath", testImagePath);
//
//        Processor showCurrentWeatherProcessor = new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);
//        User user = new User(1L, UTC0_LONGITUDE, UTC0_LATITUDE, LocalTime.of(0, 0),
//                "testUserId", "testUserName", false);
//
//        ReflectionTestUtils.setField(imageSender, "imagePath", errorImagePath);
//
//        userRepository.save(user);
//
//        MessageDTO messageDTO = getMessageDTO(user);
//        showCurrentWeatherProcessor.process(messageDTO);
//
//        verify(imageSender).sendImageToTheUser(anyLong());
//        Command command = showCurrentWeatherProcessor.getCommandType();
//        assertEquals(Command.SHOW_CURRENT_WEATHER, command);
//    }

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

    ImageSender getImageSenderSpy(ScreenshotterClient screenshotterClient) {
        UserService userService = new UserService(userRepository);
        ImageSender imageSender = new ImageSender(userService, screenshotterClient, spyBot);
        return spy(imageSender);
    }
}
