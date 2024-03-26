package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import eu.ciechanowiec.bot.utils.KeyboardCreator;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
public class StartProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final MessageTemplater messageTemplater;
    private final Command command;
    private final KeyboardCreator keyboardCreator;

    @Autowired
    StartProcessor(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.messageTemplater = new MessageTemplater();
        command = Command.START;
        keyboardCreator = new KeyboardCreator();
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();
        Chat chat = message.getChat();
        String userName = chat.getFirstName();
        String messageToSend = messageTemplater.getGreetMessage(userName);
        Long chatId = message.getChatId();
        String chatIdStr = String.valueOf(chatId);
        String userId = chat.getUserName();
        SendMessage sendMessage = new SendMessage(chatIdStr, messageToSend);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardCreator.createReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        telegramBot.execute(sendMessage);

        if (userService.isUserExists(chatId)) {
            MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
            telegramBot.onUpdateReceived(messageWithNewType);
        } else {
            userService.createUserWithChatIdAndUserInfo(chatId, userId, userName);

            MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.ASK_LOCATION);
            telegramBot.onUpdateReceived(messageWithNewType);
        }
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
