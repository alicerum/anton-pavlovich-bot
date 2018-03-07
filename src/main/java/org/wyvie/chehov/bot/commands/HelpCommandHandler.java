package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

public class HelpCommandHandler implements CommandHandler {

    private TelegramBot telegramBot;

    public static final String COMMAND = "help";

    private static final String HELP_TEXT =
            "I don't know how to help you, my child. I am deeply sorry.";

    public HelpCommandHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void handle(Message message, String args) {
        SendMessage sendMessage = new SendMessage(message.chat().id(), HELP_TEXT);

        telegramBot.execute(sendMessage);
    }
}
