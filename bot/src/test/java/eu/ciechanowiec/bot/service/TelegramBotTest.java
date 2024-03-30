package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import lombok.SneakyThrows;
import org.jboss.jandex.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = Main.class)
@Profile("test")
class TelegramBotTest {

    @MockBean
    private TelegramBot spyBot;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ReturnOfNull", "ChainedMethodCall"})
    @SneakyThrows
    @Test
    void onUpdateReceivedShouldSendMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        Chat chat = new Chat();
        chat.setId(1L);
        Message message = new Message();
        message.setChat(chat);
        message.setText("/config");
        Update update = new Update();
        update.setMessage(message);
        spyBot.onUpdateReceived(update);

        TelegramBot verify = verify(spyBot);
        verify.onUpdateReceived(update);
    }
}
