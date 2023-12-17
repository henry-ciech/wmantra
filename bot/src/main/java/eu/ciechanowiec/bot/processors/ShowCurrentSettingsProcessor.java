package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.*;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import eu.ciechanowiec.bot.service.WeatherLocationService;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import eu.ciechanowiec.bot.utils.WeatherScheduler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
class ShowCurrentSettingsProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final WeatherLocationService weatherLocationService;
    private final WeatherScheduler weatherScheduler;
    private final MessageTemplater messageTemplater;

    @Autowired
    ShowCurrentSettingsProcessor(TelegramBot telegramBot, UserService userService,
                                 WeatherLocationService weatherLocationService,
                                 WeatherScheduler weatherScheduler, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.weatherLocationService = weatherLocationService;
        this.weatherScheduler = weatherScheduler;
        this.messageTemplater = messageTemplater;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();
        Long chatId = message.getChatId();
        ConfigurationStage configurationStage = userService.getConfigurationStage(chatId);

        switch (configurationStage) {
            case COMPLETED -> handleCompletedConfiguration(chatId);
            case NO_TIME -> handleNoTimeConfiguration(messageDTO);
            case NO_LOCATION_AND_TIME -> handleAskLocationConfiguration(messageDTO);
        }
    }

    @Override
    public Command getCommandType() {
        return Command.SHOW_CURRENT_SETTINGS;
    }

    @SneakyThrows
    private void handleCompletedConfiguration(Long chatId) {
        User user = userService.getUser(chatId);
        User userWithAdjustedTime = getUserWithAdjustedTime(chatId);
        String messageToSend = prepareMessageTextToSend(userWithAdjustedTime);
        SendMessage sendMessage = createSendMessage(chatId, messageToSend);
        weatherScheduler.schedule(user);
        telegramBot.execute(sendMessage);
    }

    private User adjustUserTime(User user, ZoneOffset zoneOffset) {
        LocalTime utcTime = user.getTime();
        OffsetTime timeInUTCOffset = utcTime.atOffset(ZoneOffset.UTC);
        OffsetTime adjustedTime = timeInUTCOffset.withOffsetSameInstant(zoneOffset);
        return new User(user, adjustedTime.toLocalTime());
    }

    private User getUserWithAdjustedTime(Long chatId) {
        double latitude = userService.getLatitude(chatId);
        double longitude = userService.getLongitude(chatId);
        User user = userService.getUser(chatId);
        ZoneOffset zoneOffset = userService.getTimeZone(longitude, latitude);
        return adjustUserTime(user, zoneOffset);
    }

    private void handleNoTimeConfiguration(MessageDTO messageDTO) {
        MessageDTO changedMessage = messageDTO.withNewMessageType(Command.ASK_TIME);
        telegramBot.onUpdateReceived(changedMessage);
    }

    private void handleAskLocationConfiguration(MessageDTO messageDTO) {
        MessageDTO changedMessage = messageDTO.withNewMessageType(Command.ASK_LOCATION);
        telegramBot.onUpdateReceived(changedMessage);
    }

    private String prepareMessageTextToSend(User user) {
        Double latitude = user.getLatitude();
        Double longitude = user.getLongitude();
        LocationData locationData = weatherLocationService.getLocationData(latitude, longitude);
        return messageTemplater.getWhenGettingMessage(locationData, user);
    }

    private SendMessage createSendMessage(Long chatId, String messageText) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());
        return sendMessage;
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add("🌥 Show current weather");
        secondRow.add("⚙️ Configure");
        keyboardRows.add(firstRow);
        keyboardRows.add(secondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
