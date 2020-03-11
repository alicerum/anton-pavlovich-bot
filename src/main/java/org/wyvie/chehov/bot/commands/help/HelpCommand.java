package org.wyvie.chehov.bot.commands.help;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.bot.commands.CommandHandler;

import java.util.ArrayList;
import java.util.List;

@Service
public class HelpCommand implements CommandHandler {
    private static final String COMMAND = "help";

    private final Logger logger = LoggerFactory.getLogger(HelpCommand.class);
    private final TelegramBot telegramBot;
    private List<String> commandNames = new ArrayList<>();

    @Autowired
    public HelpCommand(@Qualifier("telegramBot") TelegramBot telegramBot, List<CommandHandler> commandHandlers) {
        this.telegramBot = telegramBot;
        commandHandlers.forEach(commandHandler ->
        {
            commandNames.add(commandHandler.getCommand().replace("karma", ""));
            commandNames.sort(String::compareTo);
        });
    }

    @Override
    public void handle(Message message, String args) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Список команд бота:</b>");
        sb.append("\n");
        commandNames.forEach(cn -> {
            sb.append("<b>");
            if (!cn.equals("+") && !cn.equals("-")) {
                sb.append("/");
            }
            sb.append(cn);
            sb.append("</b>");
            sb.append("\n");
        });
        long messageChatId = message.chat().id();
        sendMessage(messageChatId, sb.toString().trim());
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        sendMessage = sendMessage.parseMode(ParseMode.HTML);
        SendResponse response = telegramBot.execute(sendMessage);
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
