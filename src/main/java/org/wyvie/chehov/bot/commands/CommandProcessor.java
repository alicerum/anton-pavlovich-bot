package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.dailymenu.DailyMenuCommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommandProcessor {

    private final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    private Map<String, CommandHandler> handlers = new HashMap<>();

    @Autowired
    public CommandProcessor(@Qualifier("telegramBot")TelegramBot telegramBot,
                            List<CommandHandler> commandHandlers) {

        commandHandlers.forEach(commandHandler -> handlers.put(commandHandler.getCommand(), commandHandler));
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

        String finalArguments = arguments;
        getCommandHandler(command).ifPresent(handler -> {
            handler.handle(message, finalArguments);
        });
    }

    private Optional<CommandHandler> getCommandHandler(String command) {

        return Optional.ofNullable(handlers.get(command));
    }

}
