package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

public class HelpCommandHandler implements CommandHandler {

    private TelegramBot telegramBot;

    private static final String HELP_TEXT =
            "I don't know how to help you, my child. I am deeply sorry.";

    public HelpCommandHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void handle(long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, HELP_TEXT);

        telegramBot.execute(sendMessage);
    }
}
