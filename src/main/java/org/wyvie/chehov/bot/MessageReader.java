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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wyvie.chehov.TelegramProperties;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MessageReader {

    private final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private MessageProcessor messageProcessor;
    private TelegramBot telegramBot;
    private TelegramProperties telegramProperties;

    private int lastOffset;

    @Autowired
    public MessageReader(MessageProcessor messageProcessor,
                         TelegramProperties telegramProperties,
                         @Qualifier("telegramBot") TelegramBot telegramBot) {

        this.messageProcessor = messageProcessor;
        this.telegramBot = telegramBot;
        this.telegramProperties = telegramProperties;

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
            if (messageProcessor.processMessage(update.message()))
                lastOffset = update.updateId() + 1;
        });
    }
}
