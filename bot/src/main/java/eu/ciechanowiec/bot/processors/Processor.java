package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;

public interface Processor {

    void process(MessageDTO messageDTO);
    
    Command getCommandType();
}
