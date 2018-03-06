package org.wyvie.chehov.bot;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

    private final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

    public boolean processMessage(Message message) {
        logger.debug("Got message '" + message.text() + "' from chat_id " + message.chat().id());

        return true;
    }
}
