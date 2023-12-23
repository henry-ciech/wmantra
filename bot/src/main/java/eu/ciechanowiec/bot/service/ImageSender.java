package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImageSender {

    private final UserService userService;
    private final TelegramBot telegramBot;

    @Value("${image.path}")
    private String imagePath;

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    public ImageSender(ApplicationContext applicationContext, UserService userService) {
        this.userService = userService;
        this.telegramBot = applicationContext.getBean(TelegramBot.class);
    }

    @SneakyThrows
    private ByteArrayInputStream getImageAsInputStream(double longitude, double latitude) {
        String url = String.format("%s/take-screenshot?longitude=%f&latitude=%f", baseUrl, longitude, latitude);
        RestOperations restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        Optional<byte[]> imageBytesNullable = Optional.ofNullable(response.getBody());
        return imageBytesNullable.map(ByteArrayInputStream::new).orElse(getErrorPhoto());
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

        ByteArrayInputStream inputStream = getImageAsInputStream(longitude, latitude);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatIdStr);

        InputFile inputFile = new InputFile(inputStream, "image.png");
        photo.setPhoto(inputFile);
        telegramBot.execute(photo);
    }
}
