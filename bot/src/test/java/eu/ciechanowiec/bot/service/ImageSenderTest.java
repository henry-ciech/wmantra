package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;

@SpringBootTest
class ImageSenderTest {

    private static final String IMAGE_PATH = "/home/henryk/0_prog/wmantra/bot/src/test/resources/errorImage.png";
    @Captor
    ArgumentCaptor<SendPhoto> photoCaptor;

    @SneakyThrows
    @Test
    void errorShouldBeSent() {
        // Mock dependencies
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        UserService mockUserService = Mockito.mock(UserService.class);
        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);

        // Initialize ImageSender
        ImageSender imageSender = new ImageSender(mockUserService, mockScreenshoterClient, mockTelegramBot);

        // Set up test data
        String testImagePath = "/errorImage.png"; // Path relative to resources folder
        InputStream testImageStream = getClass().getResourceAsStream(testImagePath);

        // Use ReflectionTestUtils to inject test image path
        ReflectionTestUtils.setField(imageSender, "imagePath", getClass().getResource(testImagePath).getPath());

        // Mock method to return test image stream
        Mockito.when(mockScreenshoterClient.getImageAsInputStream(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any()))
                .thenReturn(new ByteArrayInputStream(IOUtils.toByteArray(testImageStream)));

        // Invoke the method under test
        imageSender.sendImageToTheUser(1L);

        // Capture and verify the sent photo
        ArgumentCaptor<SendPhoto> photoCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(mockTelegramBot).execute(photoCaptor.capture());
        SendPhoto sentPhoto = photoCaptor.getValue();

        // Convert and compare byte arrays
        byte[] sentBytes = IOUtils.toByteArray((InputStream) sentPhoto.getPhoto().getNewMediaStream());
        byte[] expectedBytes = IOUtils.toByteArray(getClass().getResourceAsStream(testImagePath));
        assertArrayEquals(expectedBytes, sentBytes);
    }


    @SneakyThrows
    @Test
    void imageShouldBeSent() {
        // Mock dependencies
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        UserService mockUserService = Mockito.mock(UserService.class);
        ScreenshoterClient mockScreenshoterClient = Mockito.mock(ScreenshoterClient.class);

        // Initialize ImageSender
        ImageSender imageSender = new ImageSender(mockUserService, mockScreenshoterClient, mockTelegramBot);

        // Define the path to the test image
        String testImagePath = "/home/henryk/0_prog/wmantra/bot/src/main/resources/testImage.png";

        // Use ReflectionTestUtils to inject test image path into ImageSender
        ReflectionTestUtils.setField(imageSender, "imagePath", testImagePath);

        // Read the test image file into a byte array
        byte[] testImageBytes = Files.readAllBytes(Paths.get(testImagePath));

        // Mock method to return test image stream
        Mockito.when(mockScreenshoterClient.getImageAsInputStream(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any()))
                .thenReturn(new ByteArrayInputStream(testImageBytes));

        // Invoke the method under test
        imageSender.sendImageToTheUser(1L);

        // Capture and verify the sent photo
        ArgumentCaptor<SendPhoto> photoCaptor = ArgumentCaptor.forClass(SendPhoto.class);
        verify(mockTelegramBot).execute(photoCaptor.capture());
        SendPhoto sentPhoto = photoCaptor.getValue();

        // Convert and compare byte arrays
        byte[] sentBytes = IOUtils.toByteArray((InputStream) sentPhoto.getPhoto().getNewMediaStream());
        assertArrayEquals(testImageBytes, sentBytes);
    }
}
