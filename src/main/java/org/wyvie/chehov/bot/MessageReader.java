package org.wyvie.chehov.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.TelegramProperties;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MessageReader {

    private final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private CommandProcessor commandProcessor;
    private TelegramBot telegramBot;
    private TelegramProperties telegramProperties;
    private String botUsername;

    private int lastOffset;

    @Autowired
    public MessageReader(CommandProcessor commandProcessor,
                         TelegramProperties telegramProperties,
                         @Qualifier("telegramBot") TelegramBot telegramBot,
                         @Qualifier("botUsername") String botUsername) {

        this.commandProcessor = commandProcessor;
        this.telegramBot = telegramBot;
        this.telegramProperties = telegramProperties;
        this.botUsername = botUsername;

        this.lastOffset = 0;
    }

    @Scheduled(fixedDelay = 200)
    public void readMessages() {
        GetUpdates getUpdates = new GetUpdates()
                .limit(telegramProperties.getUpdateLimit())
                .offset(lastOffset)
                .timeout(0);

        GetUpdatesResponse response = telegramBot.execute(getUpdates);
        List<Update> updates = response.updates();

        updates.forEach(update -> {
            lastOffset = update.updateId() + 1;

            Message message = update.message();

            logger.debug("Got message '" + message.text() + "' from chat_id " + message.chat().id());

            if (validateCommmand(message))
                commandProcessor.processCommand(message);

            lastOffset = update.updateId() + 1;
        });
    }

    /**
     * Validates this is the command we would like to process
     * @param message message from Telegram chat
     * @return true if we want to process the command, false otherwise
     */
    private boolean validateCommmand(Message message) {
        String messageText = message.text().toLowerCase();

        // if starts with '/' symbol, it's a command
        if (!StringUtils.isEmpty(messageText) &&
                messageText.startsWith("/")) {

            String command = messageText.contains(" ") ?
                    messageText.split(" ")[0] : messageText;

            // talking to another bot here
            if (command.contains("@") &&
                    !command.endsWith("@" + botUsername)) {

                return false;
            }

            return true;
        }

        // not a command
        return false;
    }
}
