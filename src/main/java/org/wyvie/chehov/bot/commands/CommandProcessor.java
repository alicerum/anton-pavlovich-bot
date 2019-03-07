package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Sticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.bot.annotations.PublicChatOnlyCommand;
import org.wyvie.chehov.bot.commands.helper.EmojiHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommandProcessor {

    private final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    private Map<String, CommandHandler> handlers = new HashMap<>();

    private final EmojiHelper emojiHelper;

    @Autowired
    public CommandProcessor(@Qualifier("telegramBot") TelegramBot telegramBot,
                            EmojiHelper emojiHelper,
                            List<CommandHandler> commandHandlers) {

        commandHandlers.forEach(commandHandler ->
                handlers.put(commandHandler.getCommand(), commandHandler));

        this.emojiHelper = emojiHelper;
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
            if (canProcessCommand(message, handler)) {
                handler.handle(message, finalArguments);
            }
        });
    }

    public void processKarma(Message message) {
        String messageText;

        Sticker sticker = message.sticker();
        if (sticker != null) {
            messageText = sticker.emoji();
        } else {
            messageText = message.text().trim();
        }

        if (emojiHelper.isThumbsUp(messageText))
            messageText = "+";
        else if (emojiHelper.isThumbsDown(messageText))
            messageText = "-";

        getCommandHandler("karma" + messageText.charAt(0)).ifPresent(handler -> {
            if (canProcessCommand(message, handler)) {
                handler.handle(message, "");
            }
        });
    }

    private boolean canProcessCommand(Message message, CommandHandler handler) {
        boolean canProcess = false;
        if (!(handler.getClass().isAnnotationPresent(PublicChatOnlyCommand.class) &&
                message.chat().type() == Chat.Type.Private)) {
            canProcess = true;
        }
        return canProcess;
    }

    private Optional<CommandHandler> getCommandHandler(String command) {

        return Optional.ofNullable(handlers.get(command));
    }

}
