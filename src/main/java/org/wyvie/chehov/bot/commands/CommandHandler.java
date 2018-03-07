package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.model.Message;

public interface CommandHandler {

    void handle(Message message, String args);
}
