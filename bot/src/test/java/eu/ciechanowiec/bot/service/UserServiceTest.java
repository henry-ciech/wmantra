
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
        UserService userService = new UserService(userRepository);

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
        userService.createUserWithChatIdAndUserInfo(3L, "newUserId", "newUserName");
        User newUser = userRepository.findUser(3L);
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
        // Assuming you have a way to create a mock Update object
        Update update = createMockUpdateWithMessage(); // mock method to create Update with specific text and chatId
        userService.saveTime(update);
        User updatedUser = userRepository.findUser(1L);

        assertAll(
                () -> assertNotNull(updatedUser.getTime()),
                () -> assertEquals(LocalTime.of(0, 0), updatedUser.getTime())
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void testUpdateTime() {
        long chatId = 1L;
        LocalTime newTime = LocalTime.of(1, 0);

        // Update time
        userService.updateTime(chatId, newTime);

        // Retrieve the user again to ensure we have the latest data
        User updatedUser = userRepository.findUser(chatId);

        // Assert the time has been updated
        assertEquals(newTime, updatedUser.getTime());
    }

    @Test
    void testDetermineConfigurationStage() {
        ConfigurationStage stage = userService.determinConfigurationStage(1L);
        assertEquals(ConfigurationStage.COMPLETED, stage); // Assuming location and time are already set for chatId 1L
    }

    @Test
    void testSaveLocation() {
        Update update = createMockUpdateWithLocation(); // mock method to create Update with specific location and chatId
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
