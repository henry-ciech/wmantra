package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.*;
import java.util.Optional;

@Service
@Slf4j
class SaveLocationProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final MessageTemplater messageTemplater;
    private final Command command;

    @Autowired
    SaveLocationProcessor(TelegramBot telegramBot, UserService userService, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.messageTemplater = messageTemplater;
        command = Command.SAVE_LOCATION;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();

        boolean timeSpecified = userService.isTimeSpecified(chatId);
        if (timeSpecified) {
            processForTimeSpecified(messageDTO);
        } else {
            log.info("Saving location");
            userService.saveLocation(update);
            MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.ASK_TIME);
            telegramBot.onUpdateReceived(messageWithNewType);
        }
    }

    @SneakyThrows
    private void processForTimeSpecified(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String chatIdStr = String.valueOf(chatId);

        LocalTime localTimeToSave = adjustTimeToLocation(message);

        userService.saveLocation(update);
        userService.updateTime(chatId, localTimeToSave);

        String messageToSend = messageTemplater.getNotifyForNewLocation();

        SendMessage sendMessage = new SendMessage(chatIdStr, messageToSend);
        telegramBot.execute(sendMessage);

        MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
        telegramBot.onUpdateReceived(messageWithNewType);
    }

    @Override
    public Command getCommandType() {
        return command;
    }

    private LocalTime adjustTimeToLocation(Message message) {
        long chatId = message.getChatId();
        User user = userService.findUser(chatId);
        double originalLongitude = userService.findLongitude(chatId);
        double originalLatitude = userService.findLatitude(chatId);
        ZoneOffset originalOffset = resolveTimeZone(originalLongitude, originalLatitude);
        LocalTime time = user.getTime();
        OffsetTime originalOffsetTimeUtc0 = OffsetTime.of(time, ZoneOffset.UTC);
        OffsetTime originalAdjustedOffsetTime = originalOffsetTimeUtc0.withOffsetSameInstant(originalOffset);

        Location newLocation = message.getLocation();
        double newLongitude = newLocation.getLongitude();
        double newLatitude = newLocation.getLatitude();
        ZoneOffset newOffset = resolveTimeZone(newLongitude, newLatitude);
        int originalHour = originalAdjustedOffsetTime.getHour();
        int originalMinute = originalAdjustedOffsetTime.getMinute();
        OffsetTime newOffsetTime = OffsetTime.of(
                originalHour, originalMinute, NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, newOffset
        );
        OffsetTime offsetTimeToSave = newOffsetTime.withOffsetSameInstant(ZoneOffset.UTC);
        return offsetTimeToSave.toLocalTime();
    }

    private ZoneOffset resolveTimeZone(double longitude, double latitude) {
        TimeZoneEngine timeZoneEngine = TimeZoneEngine.initialize();
        Optional<ZoneId> zoneIdNullable = timeZoneEngine.query(latitude, longitude);
        ZoneId zoneId = zoneIdNullable.orElse(ZoneOffset.UTC);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return now.getOffset();
    }
}
