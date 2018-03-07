package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.dailymenu.DailyMenuCommandHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandProcessor {

    private final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    private Map<String, CommandHandler> handlers = new HashMap<>();

    @Autowired
    public CommandProcessor(@Qualifier("telegramBot")TelegramBot telegramBot,
                            TelegramProperties telegramProperties) {

        handlers.put(HelpCommandHandler.COMMAND, new HelpCommandHandler(telegramBot));
        handlers.put(DailyMenuCommandHandler.COMMAND, new DailyMenuCommandHandler(telegramBot));
    }

    public void processCommand(Message message) {
        String messageText = message.text();

        String command;
        String arguments;

        if (messageText.contains(" ")) {
            int first = messageText.indexOf(' ');
            command = messageText.substring(0, first);
            arguments = messageText.substring(first);
        } else {
            command = messageText;
            arguments = "";
        }

        command = command.split("@")[0].substring(1).toLowerCase().trim();
        arguments = arguments.trim();

        getCommandHandler(command).handle(message, arguments);
    }

    private CommandHandler getCommandHandler(String command) {
        CommandHandler commandHandler = handlers.get(command);

        if (commandHandler == null)
            commandHandler = handlers.get(HelpCommandHandler.COMMAND);

        return commandHandler;
    }

}
