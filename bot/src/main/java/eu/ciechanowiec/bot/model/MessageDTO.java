package eu.ciechanowiec.bot.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record MessageDTO(Update update, Command command) {

    public Message getMessage() {
        return update.getMessage();
    }

    public MessageDTO withNewMessageType(Command commandToSet) {
        return new MessageDTO(update, commandToSet);
    }
}
