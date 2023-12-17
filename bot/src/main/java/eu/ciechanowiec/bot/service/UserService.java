package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.*;
import java.util.Optional;

@Component
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private LocalTime getLocalTimeFromOffsetTime(OffsetTime offsetTime) {
        return offsetTime.toLocalTime();
    }

    public User getUser(long chatId) {
        return userRepository.findUser(chatId);
    }

    public boolean isUserExists(long chatId) {
        return userRepository.isUserExists(chatId);
    }

    private void updateLocation(long chatId, double latitude, double longitude) {
        userRepository.updateLocation(chatId, latitude, longitude);
    }

    public Double getLatitude(long chatId) {
        return userRepository.findLatitude(chatId);
    }

    public Double getLongitude(long chatId) {
        return userRepository.findLongitude(chatId);
    }

    public void createUserWithChatIdAndUserInfo(long chatId, String userId, String userName) {
        userRepository.createUserWithChatIdAndUserInfo(chatId, userId, userName);
    }

    public boolean isTimeExists(long chatId) {
        return userRepository.existsTime(chatId);
    }

    public boolean isLocationExists(long chatId) {
        return userRepository.existsLocation(chatId);
    }

    public void updateTime(long chatId, LocalTime time) {
        userRepository.updateTime(chatId, time);
    }

    public void saveTime(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        double longitude = getLongitude(chatId);
        double latitude = getLatitude(chatId);
        String text = message.getText();

        ZoneOffset zoneOffset = getTimeZone(longitude, latitude);
        OffsetTime offsetTime = getTimeToSave(text, zoneOffset);

        LocalTime timeToSave = getLocalTimeFromOffsetTime(offsetTime);

        updateTime(chatId, timeToSave);
    }

    public ConfigurationStage getConfigurationStage(long chatId) {
        if (!isLocationExists(chatId)) {
            return ConfigurationStage.NO_LOCATION_AND_TIME;
        }

        boolean timeExistsForChatId = isTimeExists(chatId);
        return timeExistsForChatId
                ? ConfigurationStage.COMPLETED
                : ConfigurationStage.NO_TIME;
    }

    public void saveLocation(Update update) {
        Message message = update.getMessage();
        Location location = message.getLocation();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        long chatId = message.getChatId();

        updateLocation(chatId, latitude, longitude);
    }

    public ZoneOffset getTimeZone(double longitude, double latitude) {
        TimeZoneEngine timeZoneEngine = TimeZoneEngine.initialize();
        Optional<ZoneId> zoneIdNullable = timeZoneEngine.query(latitude, longitude);
        ZoneId zoneId = zoneIdNullable.orElse(ZoneOffset.UTC);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return now.getOffset();
    }

    private OffsetTime getTimeToSave(String text, ZoneOffset zoneOffset) {
        String[] hourAndMinute = text.split(":");

        String hourStr = hourAndMinute[0];
        String minuteStr = hourAndMinute[1];
        int hour = Integer.parseInt(hourStr);
        int minute = Integer.parseInt(minuteStr);

        OffsetTime offsetTime = OffsetTime.of(hour, minute, 0, 0, zoneOffset);
        return offsetTime.withOffsetSameInstant(ZoneOffset.of("+00:00"));
    }
}
