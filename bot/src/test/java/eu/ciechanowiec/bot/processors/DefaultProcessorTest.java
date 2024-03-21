package eu.ciechanowiec.bot.processors;

import static org.junit.jupiter.api.Assertions.*;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DefaultProcessorTest {

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private DefaultProcessor defaultProcessor;

    @Test
    void commandShouldBeDefault() {
        Command command = defaultProcessor.getCommandType();
        assertEquals(Command.DEFAULT, command);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        MessageTemplater messageTemplater = new MessageTemplater();
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);

        String defaultMessage = messageTemplater.getErrorMessage();
        message.setText(defaultMessage);
        update.setMessage(message);
        message.setChat(chat);
        MessageDTO messageDTO = new MessageDTO(update, Command.DEFAULT);

        defaultProcessor.process(messageDTO);

        verify(spyBot, times(1)).execute(sendMessageCaptor.capture());
        SendMessage capturedSendMessage = sendMessageCaptor.getValue();

        assertEquals(defaultMessage, capturedSendMessage.getText());
    }

    @AfterEach
    @BeforeEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }
}
