package eu.ciechanowiec.bot.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;

@Configuration
class BotRegistrar {

    private final TelegramBot bot;
    private final Environment environment;

    @Autowired
    BotRegistrar(TelegramBot bot, Environment environment) {
        this.bot = bot;
        this.environment = environment;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot((LongPollingBot) bot);
        } catch (Exception e) {
            // Log the exception or handle it appropriately
            e.printStackTrace(); // Consider using a logger instead of printing the stack trace
        }
    }
}
