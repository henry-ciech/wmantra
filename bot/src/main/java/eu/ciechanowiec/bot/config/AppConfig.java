package eu.ciechanowiec.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
class AppConfig {

    @Bean
    DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }
}
