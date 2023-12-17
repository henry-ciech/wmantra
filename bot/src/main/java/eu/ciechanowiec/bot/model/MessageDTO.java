package eu.ciechanowiec.bot.model;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public record MessageDTO(Update update, Command command) {

    public Message getMessage() {
        return update.getMessage();
    }

    public MessageDTO withNewMessageType(Command commandToSet) {
        return new MessageDTO(update, commandToSet);
    }
}
