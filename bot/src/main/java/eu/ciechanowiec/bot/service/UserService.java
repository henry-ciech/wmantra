package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private LocalTime getLocalTimeFromOffsetTime(OffsetTime offsetTime) {
        return offsetTime.toLocalTime();
    }

    public User findUser(long chatId) {
        log.info("Looking for the user in the database. Chat id: {}", chatId);
        return userRepository.findUser(chatId);
    }

    public boolean isUserExists(long chatId) {
        log.info("Check if user exists in the database. Chat id: {}", chatId);
        return userRepository.isUserExists(chatId);
    }

    public Double findLatitude(long chatId) {
        return userRepository.findLatitude(chatId);
    }

    public Double findLongitude(long chatId) {
        return userRepository.findLongitude(chatId);
    }

    public void createUserWithChatIdAndUserInfo(long chatId, String userId, String userName) {
        log.info("Creating user's record in the database. Chat id: {}; User id: {}; User name: {}"
                ,chatId, userId, userName);
        userRepository.createUserWithChatIdAndUserInfo(chatId, userId, userName);
    }

    public boolean isTimeSpecified(long chatId) {
        return userRepository.isTimeSpecified(chatId);
    }

    public boolean isLocationSpecified(long chatId) {
        return userRepository.isLocationSpecified(chatId);
    }

    public void saveTime(Update update) {
        log.info("Saving time");
        Message message = update.getMessage();
        long chatId = message.getChatId();
        double longitude = findLongitude(chatId);
        double latitude = findLatitude(chatId);
        String text = message.getText();

        ZoneOffset zoneOffset = getTimeZone(longitude, latitude);
        OffsetTime offsetTime = getTimeToSave(text, zoneOffset);

        LocalTime timeToSave = getLocalTimeFromOffsetTime(offsetTime);

        updateTime(chatId, timeToSave);
    }

    public void updateTime(long chatId, LocalTime time) {
        userRepository.updateTime(chatId, time);
    }

    public ConfigurationStage resolveConfigStage(long chatId) {
        if (!isLocationSpecified(chatId)) {
            return ConfigurationStage.NO_LOCATION_AND_TIME;
        }

        boolean isTimeSpecifiedForChatId = isTimeSpecified(chatId);
        return isTimeSpecifiedForChatId ? ConfigurationStage.COMPLETED : ConfigurationStage.NO_TIME;
    }

    public void saveLocation(Update update) {
        log.info("Saving location");
        Message message = update.getMessage();
        Location location = message.getLocation();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        long chatId = message.getChatId();

        userRepository.updateLocation(chatId, latitude, longitude);
    }

    public ZoneOffset getTimeZone(double longitude, double latitude) {
        TimeZoneEngine timeZoneEngine = TimeZoneEngine.initialize();
        Optional<ZoneId> zoneIdNullable = timeZoneEngine.query(latitude, longitude);
        ZoneId zoneId = zoneIdNullable.orElse(ZoneOffset.UTC);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return now.getOffset();
    }

    private OffsetTime getTimeToSave(CharSequence text, ZoneOffset zoneOffset) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm").withZone(zoneOffset);

        OffsetTime offsetTime = OffsetTime.parse(text, formatter);
        return offsetTime.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
