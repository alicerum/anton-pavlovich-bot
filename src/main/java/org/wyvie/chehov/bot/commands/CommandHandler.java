package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Service;

@Service
public interface CommandHandler {

    void handle(Message message, String args);
    String getCommand();
}
