package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageSender {

    private final UserService userService;
    private final TelegramBot telegramBot;
    private final ScreenshoterClient screenshoterClient;

    @Value("${image.path}")
    private String imagePath;

    @Autowired
    public ImageSender(UserService userService, ScreenshoterClient screenshoterClient, TelegramBot telegramBot) {
        this.userService = userService;
        this.telegramBot = telegramBot;
        this.screenshoterClient = screenshoterClient;
    }

    @SneakyThrows
    private ByteArrayInputStream getErrorPhoto() {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        return new ByteArrayInputStream(imageBytes);
    }

    @SneakyThrows
    public void sendImageToTheUser(long chatId) {
        SendChatAction sendChatAction = new SendChatAction();
        String chatIdStr = String.valueOf(chatId);
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(ActionType.UPLOADPHOTO);
        telegramBot.execute(sendChatAction);
        double longitude = userService.findLongitude(chatId);
        double latitude = userService.findLatitude(chatId);

        ByteArrayInputStream inputStream =
                screenshoterClient.getImageAsInputStream(longitude, latitude, getErrorPhoto());
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatIdStr);

        InputFile inputFile = new InputFile(inputStream, "image.png");
        photo.setPhoto(inputFile);
        telegramBot.execute(photo);
    }
}
