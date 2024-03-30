package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ImageSenderTest {

    @SpyBean
    private TelegramBot spyBot;
    @Captor
    private ArgumentCaptor<SendPhoto> photoCaptor;

    @Value("${error.image}")
    private String errorImageFilePath;

    @Value("${test.image.path}")
    private String testImagePath;

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull", "PMD.CloseResource"})
    @SneakyThrows
    @Test
    void imageShouldBeSent() {
        doAnswer(invocation -> null).when(spyBot).execute(any(SendChatAction.class));
        doAnswer(invocation -> null).when(spyBot).execute(any(SendPhoto.class));

        UserService userService = mock(UserService.class);
        when(userService.findLatitude(anyLong())).thenReturn(0.0);
        when(userService.findLongitude(anyLong())).thenReturn(0.0);

        ScreenshotterClient mockScreenshotterClient = mock(ScreenshotterClient.class);

        ImageSender imageSender = new ImageSender(userService, mockScreenshotterClient, spyBot);

        ReflectionTestUtils.setField(imageSender, "imagePath", testImagePath);

        Path path = Paths.get(testImagePath);
        byte[] expectedImageBytes = Files.readAllBytes(path);

        ByteArrayInputStream value = new ByteArrayInputStream(expectedImageBytes);
        when(mockScreenshotterClient.getImageAsInputStream(anyDouble(), anyDouble())).thenReturn(Optional.of(value));

        imageSender.sendImageToTheUser(1L);

        TelegramBot verify = verify(spyBot);
        verify.execute(photoCaptor.capture());
        SendPhoto sentPhoto = photoCaptor.getValue();

        InputFile photo = sentPhoto.getPhoto();
        InputStream newMediaStream = photo.getNewMediaStream();
        byte[] actualBytes = IOUtils.toByteArray(newMediaStream);
        newMediaStream.close();
        assertArrayEquals(expectedImageBytes, actualBytes);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull", "PMD.CloseResource", "PMD.NcssCount",
    "ExecutableStatementCount", "JavaNCSS", "VariableDeclarationUsageDistance", "Indentation"})
    @SneakyThrows
    @Test
    void errorShouldBeSent() {
        doAnswer(invocation -> null).when(spyBot).execute(any(SendChatAction.class));
        doAnswer(invocation -> null).when(spyBot).execute(any(SendPhoto.class));

        UserService userService = mock(UserService.class);
        when(userService.findLatitude(anyLong())).thenReturn(0.0);
        when(userService.findLongitude(anyLong())).thenReturn(0.0);

        ScreenshotterClient mockScreenshotterClient = mock(ScreenshotterClient.class);
        ImageSender imageSender = new ImageSender(userService, mockScreenshotterClient, spyBot);

        Class<? extends ImageSenderTest> runtimeClass = getClass();
        InputStream resourceAsStream = runtimeClass.getResourceAsStream(errorImageFilePath);

        URL resource = runtimeClass.getResource(errorImageFilePath);
        URL url = Objects.requireNonNull(resource);
        String path = url.getPath();
        ReflectionTestUtils.setField(imageSender, "imagePath", path);

        InputStream input = Objects.requireNonNull(resourceAsStream);
        ByteArrayInputStream value = new ByteArrayInputStream(IOUtils.toByteArray(input));
        when(mockScreenshotterClient.getImageAsInputStream(anyDouble(), anyDouble())).thenReturn(Optional.of(value));

        imageSender.sendImageToTheUser(1L);

        TelegramBot verify = verify(spyBot);
        verify.execute(photoCaptor.capture());
        SendPhoto sentPhoto = photoCaptor.getValue();

        InputFile photo = sentPhoto.getPhoto();
        InputStream newMediaStream = photo.getNewMediaStream();
        InputStream expectedBytesInput = Objects.requireNonNull(resourceAsStream);
        byte[] actualBytes = IOUtils.toByteArray(newMediaStream);
        byte[] expectedBytes = IOUtils.toByteArray(expectedBytesInput);
        value.close();
        newMediaStream.close();
        expectedBytesInput.close();
        input.close();
        resourceAsStream.close();
        assertArrayEquals(expectedBytes, actualBytes);
    }

    @AfterEach
    void resetSpy() {
        reset(spyBot);
    }
}
