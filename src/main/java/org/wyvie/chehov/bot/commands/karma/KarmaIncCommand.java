package org.wyvie.chehov.bot.commands.karma;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.database.repository.UserRepository;

@Service
public class KarmaIncCommand extends AbstractKarmaCommand {

    private static final String COMMAND_NAME = "karma+";

    @Autowired
    public KarmaIncCommand(UserRepository userRepository,
                           TelegramProperties telegramProperties,
                           TelegramBot telegramBot) {
        super(userRepository, telegramProperties, telegramBot);
    }

    @Override
    public void handle(Message message, String args) {
        processCommand(message);
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    @Override
    void processKarma(User user) {
        incUserKarma(user);
    }
}
