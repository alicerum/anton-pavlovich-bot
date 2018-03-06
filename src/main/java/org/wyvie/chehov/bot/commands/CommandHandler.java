package org.wyvie.chehov.bot.commands;

public interface CommandHandler {

    void handle(long chatId, String args);
}
