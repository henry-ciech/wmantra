package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import eu.ciechanowiec.bot.service.TelegramBot;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class ConfigTests {

    @Bean
    @Primary
    public TelegramBot telegramBot(BotConfig botConfig, DefaultBotOptions defaultBotOptions, ApplicationContext applicationContext) {
        System.out.println(1291);
        TelegramBot telegramBot = new TelegramBot(botConfig, defaultBotOptions, applicationContext);
        return Mockito.spy(telegramBot);
    }
}
