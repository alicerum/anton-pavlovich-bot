package org.wyvie.chehov.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.HelpCommandHandler;

@Component
public class CommandProcessor {

    private final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    private final TelegramBot telegramBot;

    @Autowired
    public CommandProcessor(@Qualifier("telegramBot")TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processCommand(Message message) {
        String messageText = message.text();

        String command = messageText.split("@")[0].substring(1);
        getCommandHandler(command).handle(message.chat().id());
    }

    private CommandHandler getCommandHandler(String command) {
        switch (command) {
            default:
                return new HelpCommandHandler(telegramBot);
        }
    }

}
