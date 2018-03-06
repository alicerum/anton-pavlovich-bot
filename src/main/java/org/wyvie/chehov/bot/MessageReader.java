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

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MessageReader {

    private final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private MessageProcessor messageProcessor;
    private TelegramBot telegramBot;
    private int telegramLimit;

    private int lastOffset;

    @Autowired
    public MessageReader(MessageProcessor messageProcessor,
                         @Qualifier("telegramBot") TelegramBot telegramBot,
                         @Value("${telegram.limit}") int limit) {

        this.messageProcessor = messageProcessor;
        this.telegramBot = telegramBot;
        this.telegramLimit = limit;

        this.lastOffset = 0;
    }

    @Scheduled(fixedDelay = 200)
    public void readMessages() {
        GetUpdates getUpdates = new GetUpdates().limit(this.telegramLimit).offset(lastOffset).timeout(0);

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
