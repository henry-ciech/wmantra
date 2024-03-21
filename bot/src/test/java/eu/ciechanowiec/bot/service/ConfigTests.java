package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
@SuppressWarnings({"java:S2187", "PMD.TestClassWithoutTestCases"})
public class ConfigTests {

    @Bean
    @Primary
    public TelegramBot telegramBot(BotConfig botConfig, DefaultBotOptions defaultBotOptions,
                                   ApplicationContext applicationContext) {
        TelegramBot telegramBot = new TelegramBot(botConfig, defaultBotOptions, applicationContext);
        return Mockito.spy(telegramBot);
    }
}
