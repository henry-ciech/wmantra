package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
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
    private TelegramBot telegramBot;
    @Autowired
    private UnknownProcessor unknownProcessor;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;
    @Captor
    private ArgumentCaptor<MessageDTO> messageDTOCaptor;

    @SneakyThrows
    @Test
    void shouldSendErrorMessage() {
        MessageTemplater messageTemplater = new MessageTemplater();
        Mockito.doAnswer(invocation -> {
            return null;
        }).when(telegramBot).execute(any(SendMessage.class));
        TelegramBot telegramBot1 = telegramBot;
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.UNKNOWN);
        unknownProcessor.process(messageDTO);

        verify(telegramBot, times(7)).execute(sendMessageCaptor.capture());
        verify(telegramBot, times(5)).onUpdateReceived(messageDTOCaptor.capture());

        List<MessageDTO> allValues = messageDTOCaptor.getAllValues();
        List<MessageDTO> capturedMessageDTO = allValues;

        assertEquals(Command.SHOW_CURRENT_SETTINGS, capturedMessageDTO.get(2).command());

        List<SendMessage> sendMessage = sendMessageCaptor.getAllValues();
        for (int i = 0; i < sendMessage.size(); i++) {
            System.out.println(i);
            System.out.println(sendMessage.get(i).getText());
        }
        String errorText = sendMessage.get(5).getText();
        assertEquals(errorText, messageTemplater.getErrorMessage());

        String askLocationText = sendMessage.get(3).getText();
        assertEquals(askLocationText, messageTemplater.getAskLocationMessage());
    }

    @Test
    void commandShouldBeAskLocation() {
        Command command = unknownProcessor.getCommandType();
        assertEquals(Command.UNKNOWN, command);
    }
}
