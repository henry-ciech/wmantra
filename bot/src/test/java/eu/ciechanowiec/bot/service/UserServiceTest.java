
package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        testUser = new User(1L, 0.0, 0.0, LocalTime.of(0, 0),
                "testUserId", "testUserName", false);
        userRepository.save(testUser);
    }

    @Test
    void testFindUser() {
        long chatId = 1L;

        User foundUser = userService.findUser(chatId);

        assertEquals(testUser, foundUser);
    }

    @Test
    void testIsUserExists() {
        assertAll(
                () -> assertTrue(userService.isUserExists(1L)),
                () -> assertFalse(userService.isUserExists(2L))
        );
    }

    @Test
    void testFindLatitude() {
        assertAll(
                () -> assertEquals(0.0, userService.findLatitude(1L)),
                () -> assertNull(userService.findLatitude(2L))
        );
    }

    @Test
    void testFindLongitude() {
        assertAll(
                () -> assertEquals(0.0, userService.findLongitude(1L)),
                () -> assertNull(userService.findLongitude(2L))
        );
    }

    @Test
    void testCreateUserWithChatIdAndUserInfo() {
        userService.createUserWithChatIdAndUserInfo(2L, "newUserId", "newUserName");
        User newUser = userRepository.findUser(2L);
        assertAll(
                () -> assertNotNull(newUser),
                () -> assertEquals("newUserId", newUser.getUserId()),
                () -> assertEquals("newUserName", newUser.getUserName())
        );
    }

    @Test
    void testIsTimeSpecified() {
        assertAll(
                () -> assertTrue(userService.isTimeSpecified(1L)),
                () -> {
                    userService.updateTime(1L, null);
                    assertFalse(userService.isTimeSpecified(1L));
                }
        );
    }

    @Test
    void testIsLocationSpecified() {
        assertAll(
                () -> assertTrue(userService.isLocationSpecified(1L)),
                () -> {
                    userRepository.createUserWithChatIdAndUserInfo(2L, "userId", "userName");
                    assertFalse(userService.isLocationSpecified(2L));
                }
        );
    }

    @Test
    void testSaveTime() {
        Update update = createMockUpdateWithMessage();
        userService.saveTime(update);
        User updatedUser = userRepository.findUser(1L);

        LocalTime time = updatedUser.getTime();
        assertAll(
                () -> assertNotNull(time),
                () -> assertEquals(LocalTime.of(0, 0), time)
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void testUpdateTime() {
        long chatId = 1L;
        LocalTime newTime = LocalTime.of(1, 0);

        userService.updateTime(chatId, newTime);

        User updatedUser = userRepository.findUser(chatId);

        LocalTime time = updatedUser.getTime();
        assertEquals(newTime, time);
    }

    @Test
    void testDetermineConfigurationStage() {
        ConfigurationStage stage = userService.resolveConfigStage(1L);
        assertEquals(ConfigurationStage.COMPLETED, stage);
    }

    @Test
    void testSaveLocation() {
        Update update = createMockUpdateWithLocation();
        userService.saveLocation(update);
        User updatedUser = userRepository.findUser(1L);

        assertAll(
                () -> assertEquals(0.0, updatedUser.getLatitude()),
                () -> assertEquals(0.0, updatedUser.getLongitude())
        );
    }

    private Update createMockUpdateWithMessage() {
        Message message = new Message();
        message.setText("00:00");
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        return update;
    }

    private Update createMockUpdateWithLocation() {
        Location location = new Location();
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        message.setLocation(location);
        Update update = new Update();
        update.setMessage(message);
        return update;
    }
}
