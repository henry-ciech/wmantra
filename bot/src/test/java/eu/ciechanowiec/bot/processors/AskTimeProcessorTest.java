package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class AskTimeProcessorTest {

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Autowired
    private AskTimeProcessor askTimeProcessor;
    @Autowired
    TelegramBot telegramBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void commandShouldBeAskTime() {
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        Processor processor = new AskTimeProcessor(mockTelegramBot);

        Command command = processor.getCommandType();
        assertEquals(Command.ASK_TIME, command);
    }

    @SuppressWarnings("ChainedMethodCall")
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        MessageTemplater messageTemplater = new MessageTemplater();
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);

        String askTimeMessage = messageTemplater.getAskTimeMessage();
        message.setText(askTimeMessage);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.ASK_TIME);

        askTimeProcessor.process(messageDTO);

        sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture());
        SendMessage capturedSendMessage = sendMessageCaptor.getAllValues().get(1);

        assertEquals(askTimeMessage, capturedSendMessage.getText());
    }
}
