package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Configuration
@SuppressWarnings({"java:S2187", "PMD.TestClassWithoutTestCases"})
@Profile("test")
public class ConfigTests {

    @Bean
    @Primary
    public DefaultBotOptions defaultBotOptions() {
        return mock(DefaultBotOptions.class);
    }

    @Bean
    BotConfig botConfig() {
        return new BotConfig("test.name", "test.token");
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @SneakyThrows
    @Bean
    @Primary
    public TelegramBot telegramBot(ApplicationContext applicationContext) {
        BotConfig botConfig = new BotConfig("test.name", "test.token");
        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
        TelegramBot telegramBot = new TelegramBot(botConfig, defaultBotOptions, applicationContext);
        TelegramBot mockedBot = Mockito.spy(telegramBot);
        doNothing().when(mockedBot).clearWebhook();
        return mockedBot;
    }
}
