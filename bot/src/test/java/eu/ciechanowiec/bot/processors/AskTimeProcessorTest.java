package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class AskTimeProcessorTest {

    @Autowired
    private AskTimeProcessor askTimeProcessor;
    @SpyBean
    private TelegramBot spyBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @Test
    void commandShouldBeAskTime() {
        Command command = askTimeProcessor.getCommandType();
        assertEquals(Command.ASK_TIME, command);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        MessageTemplater messageTemplater = new MessageTemplater();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);

        String askTimeMessage = messageTemplater.getAskTimeMessage();
        message.setText(askTimeMessage);
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.ASK_TIME);

        askTimeProcessor.process(messageDTO);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(spyBot, times(1)).execute(sendMessageCaptor.capture());
        SendMessage capturedSendMessage = sendMessageCaptor.getValue();

        assertEquals(askTimeMessage, capturedSendMessage.getText());
    }
}
