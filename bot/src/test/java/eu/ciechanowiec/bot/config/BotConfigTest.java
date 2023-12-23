package eu.ciechanowiec.bot.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BotConfigTest {

    @Autowired
    private BotConfig botConfig;

    @Test
    void testBotName() {
        String botName = botConfig.getBotName();
        assertEquals("WeatherMantraBot", botName);
    }

    @Test
    void testBotToken() {
        String botToken = botConfig.getToken();
        assertEquals("6637606301:AAHyBGs3C6JoQ-27fQuRPwOKqFDxkMk8nz4", botToken);
    }
}
