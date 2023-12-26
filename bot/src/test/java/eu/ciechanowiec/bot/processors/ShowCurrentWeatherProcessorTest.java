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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class ShowCurrentWeatherProcessorTest {

    @Autowired
    TelegramBot spyBot;
    @Autowired
    UserRepository userRepository;
    @Captor
    ArgumentCaptor<MessageDTO> messageDTOCaptor;

    @SneakyThrows
    @Test
    void redirectToShowCurrentSettings() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));

        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);
        ImageSender imageSender = getImageSenderSpy(mockScreenshoterClient);
        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.determinConfigurationStage(anyLong())).
                thenReturn(ConfigurationStage.NO_LOCATION_AND_TIME);

        ShowCurrentWeatherProcessor showCurrentWeatherProcessor =
                new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
        showCurrentWeatherProcessor.process(messageDTO);
        verify(spyBot, times(4)).onUpdateReceived(messageDTOCaptor.capture());
        Command command = messageDTOCaptor.getAllValues().get(2).command();

        assertEquals(Command.SHOW_CURRENT_SETTINGS, command);
    }

    @SneakyThrows
    @Test
    void shouldSendImage() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendChatAction.class));
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendPhoto.class));
        UserService userService = new UserService(userRepository);

        // Initialize ImageSender

        // Define the path to the test image
        String testImagePath = "/home/henryk/0_prog/wmantra/bot/src/main/resources/testImage.png";

        // Use ReflectionTestUtils to inject test image path into ImageSender

        // Read the test image file into a byte array
        byte[] testImageBytes = Files.readAllBytes(Paths.get(testImagePath));

        // Mock method to return test image stream
        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);
        Mockito.when(mockScreenshoterClient.getImageAsInputStream(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any()))
                .thenReturn(new ByteArrayInputStream(testImageBytes));
        ImageSender imageSender = getImageSenderSpy(mockScreenshoterClient);
        ReflectionTestUtils.setField(imageSender, "imagePath", testImagePath);

        ShowCurrentWeatherProcessor showCurrentWeatherProcessor =
                new ShowCurrentWeatherProcessor(imageSender, spyBot, userService);
        User user = new User(1L, -0.118092, 51.509865, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);

        ReflectionTestUtils.setField(imageSender, "imagePath",
                "/home/henryk/0_prog/wmantra/bot/src/main/resources/errorImage.png");

        userRepository.save(user);

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
        MessageDTO messageDTO = new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
        showCurrentWeatherProcessor.process(messageDTO);

        verify(imageSender).sendImageToTheUser(anyLong());
        Command command = showCurrentWeatherProcessor.getCommandType();
        assertEquals(Command.SHOW_CURRENT_WEATHER, command);
    }

    ImageSender getImageSenderSpy(ScreenshoterClient screenshoterClient) {
        UserService userService = new UserService(userRepository);
        ImageSender imageSender = new ImageSender(userService, screenshoterClient, spyBot);
        return Mockito.spy(imageSender);
    }
}
