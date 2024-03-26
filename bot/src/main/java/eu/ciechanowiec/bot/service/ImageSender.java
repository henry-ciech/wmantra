package eu.ciechanowiec.bot.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.ciechanowiec.bot.utils.KeyboardCreator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Slf4j
public class ImageSender {

    private final UserService userService;
    private final TelegramBot telegramBot;
    private final ScreenshotterClient screenshotterClient;
    private final KeyboardCreator keyboardCreator;

    @Value("${error.image.path}")
    private String imagePath;

    @Autowired
    public ImageSender(UserService userService, ScreenshotterClient screenshotterClient, TelegramBot telegramBot) {
        this.userService = userService;
        this.telegramBot = telegramBot;
        this.screenshotterClient = screenshotterClient;
        keyboardCreator = new KeyboardCreator();
    }

    @SneakyThrows
    private ByteArrayInputStream getErrorPhoto() {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        log.info("Sending error photo");

        return new ByteArrayInputStream(imageBytes);
    }

    @SneakyThrows
    public void sendImageToTheUser(long chatId) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(ActionType.UPLOADPHOTO);
        telegramBot.execute(sendChatAction);
        double longitude = userService.findLongitude(chatId);
        double latitude = userService.findLatitude(chatId);

        log.info("Prepare the image");
        Optional<ByteArrayInputStream> inputStream = screenshotterClient.getImageAsInputStream(longitude, latitude);
        ByteArrayInputStream photoToSend = inputStream.orElseGet(this::getErrorPhoto);

        InputFile inputFile = new InputFile(photoToSend, "image.png");
        SendPhoto photo = new SendPhoto(String.valueOf(chatId), inputFile);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardCreator.createReplyKeyboardMarkup();
        photo.setReplyMarkup(replyKeyboardMarkup);
        log.info("Send image to the user");

        telegramBot.execute(photo);
        log.info("photo sent");
    }
}
