package eu.ciechanowiec.bot.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("test")
class BotConfigTest {

    @Autowired
    private BotConfig botConfig;

    @Test
    void testBotName() {
        String botName = botConfig.getBotName();
        assertEquals("test.name", botName);
    }

    @Test
    void testBotToken() {
        String botToken = botConfig.getToken();
        assertEquals("test.token", botToken);
    }
}
