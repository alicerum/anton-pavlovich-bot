package org.wyvie.chehov.bot.commands.dailymenu;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyvie.chehov.bot.commands.CommandHandler;

public class DailyMenuCommandHandler implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(DailyMenuCommandHandler.class);

    public static final String COMMAND = "dailymenu";

    private TelegramBot telegramBot;

    public DailyMenuCommandHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void handle(long chatId, String args) {
        logger.debug("args is '" + args + "'");
    }
}
