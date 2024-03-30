package eu.ciechanowiec.bot.processors;

import static org.junit.jupiter.api.Assertions.*;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConfigProcessorTest {

    @SpyBean
    private TelegramBot spyBot;
    @Autowired
    private ConfigProcessor configProcessor;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @Test
    void commandShouldBeConfig() {
        Command command = configProcessor.getCommandType();
        assertEquals(Command.CONFIG, command);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        MessageTemplater messageTemplater = new MessageTemplater();
        Chat chat = new Chat();
        chat.setId(1L);

        String configMessage = messageTemplater.getAskConfigMessage();
        Message message = new Message();
        message.setText(configMessage);
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.CONFIG);

        configProcessor.process(messageDTO);

        ArgumentCaptor<SendMessage> sendMessageCaptor;
        sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(spyBot, times(1)).execute(sendMessageCaptor.capture());
        SendMessage capturedSendMessage = sendMessageCaptor.getValue();

        assertEquals(configMessage, capturedSendMessage.getText());
    }
}
