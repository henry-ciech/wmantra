package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class UnknownProcessorTest {

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private UnknownProcessor unknownProcessor;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;
    @Captor
    private ArgumentCaptor<MessageDTO> messageDTOCaptor;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void shouldSendErrorMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.UNKNOWN);
        unknownProcessor.process(messageDTO);

        verify(spyBot, times(2)).execute(sendMessageCaptor.capture());
        verify(spyBot, times(2)).onUpdateReceived(messageDTOCaptor.capture());

        List<MessageDTO> allValues = messageDTOCaptor.getAllValues();
        MessageDTO actualMessageDTO = allValues.get(0);
        assertEquals(Command.SHOW_CURRENT_SETTINGS, actualMessageDTO.command());

        List<SendMessage> sendMessage = sendMessageCaptor.getAllValues();
        SendMessage actualSendMessage = sendMessage.get(0);
        String errorText = actualSendMessage.getText();
        MessageTemplater messageTemplater = new MessageTemplater();
        assertEquals(errorText, messageTemplater.getWrongDataMessage());
    }

    @Test
    void commandShouldBeAskLocation() {
        Command command = unknownProcessor.getCommandType();
        assertEquals(Command.UNKNOWN, command);
    }
}
