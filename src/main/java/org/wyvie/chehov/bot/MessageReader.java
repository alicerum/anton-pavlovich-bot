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

    private TelegramBot telegramBot;

    private int telegramLimit;

    private int lastOffset;

    @Autowired
    public MessageReader(@Qualifier("telegramBot") TelegramBot telegramBot,
                         @Value("${telegram.limit}") String limit) {

        this.telegramBot = telegramBot;

        try {
            this.telegramLimit = Integer.parseInt(limit, 10);
        } catch (NumberFormatException e) {
            this.telegramLimit = 300;
        }

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
            System.out.println("new message here " + message.text());
            logger.debug("New message here: " + message.text());
        });
    }
}
