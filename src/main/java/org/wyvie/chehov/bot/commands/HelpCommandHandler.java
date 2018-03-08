package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class HelpCommandHandler implements CommandHandler {

    private TelegramBot telegramBot;

    private static final String COMMAND = "help";

    private static final String HELP_TEXT =
            "I don't know how to help you, my child. I am deeply sorry.";

    @Autowired
    public HelpCommandHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public void handle(Message message, String args) {
        SendMessage sendMessage = new SendMessage(message.chat().id(), HELP_TEXT);

        telegramBot.execute(sendMessage);
    }
}
