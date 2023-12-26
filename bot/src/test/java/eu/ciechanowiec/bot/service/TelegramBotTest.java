package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TelegramBotTest {

    @Autowired
    TelegramBot spyBot;

    @SneakyThrows
    @Test
    void testOnUpdateReceived() {
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(spyBot).execute(any(SendMessage.class));

        Update update = new Update();
        Chat chat = new Chat();
        chat.setId(1L);
        Message message = new Message();
        message.setChat(chat);
        message.setText("/config");
        update.setMessage(message);
        spyBot.onUpdateReceived(update);

        verify(spyBot).onUpdateReceived(new MessageDTO(update, Command.CONFIG));
    }
}
