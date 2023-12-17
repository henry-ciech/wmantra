package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.*;
import java.util.Optional;

@Component
public class ImageService {

    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;
    private final UserService userService;
    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    public ImageService(ApplicationContext applicationContext, UserService userService) {
        this.userService = userService;
        this.applicationContext = applicationContext;
        this.restTemplate = new RestTemplate();
    }

    @SneakyThrows
    private Optional<ByteArrayInputStream> getImageAsInputStream(double longitude, double latitude) {
        String url = String.format("%s/take-screenshot?longitude=%f&latitude=%f", baseUrl, longitude, latitude);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        Optional<byte[]> imageBytesNullable = Optional.ofNullable(response.getBody());
        if (imageBytesNullable.isPresent()) {
            byte[] imageBytes = imageBytesNullable.get();
            return Optional.of(new ByteArrayInputStream(imageBytes));
        }
        return Optional.empty();
    }

    @SneakyThrows
    public void sendImageToTheUser(long chatId) {
        TelegramBot telegramBot = applicationContext.getBean(TelegramBot.class);
        SendChatAction sendChatAction = new SendChatAction();
        String chatIdStr = String.valueOf(chatId);
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(ActionType.UPLOADPHOTO);
        telegramBot.execute(sendChatAction);
        SendPhoto photo = new SendPhoto();
        double longitude = userService.getLongitude(chatId);
        double latitude = userService.getLatitude(chatId);

        photo.setChatId(chatIdStr);

        Optional<ByteArrayInputStream> optionalStream = getImageAsInputStream(longitude, latitude);

        boolean isPresent = optionalStream.isPresent();
        if (isPresent) {
            ByteArrayInputStream inputStream = optionalStream.get();
            InputFile inputFile = new InputFile(inputStream, "image.png");
            photo.setPhoto(inputFile);
            telegramBot.execute(photo);
        } else {
            SendMessage sendMessage = new SendMessage(chatIdStr, "Service unavailable");
            telegramBot.execute(sendMessage);
        }
    }
}
