package org.wyvie.chehov.bot.commands.karma;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.UserRepository;

@Service
public class Dno10Command extends AbstractKarmaCommand {

    private final Logger logger = LoggerFactory.getLogger(Dno10Command.class);

    private static final String COMMAND_NAME = "dno10";

    @Autowired
    public Dno10Command(UserRepository userRepository,
                        TelegramProperties telegramProperties,
                        TelegramBot telegramBot) {

        super(userRepository, telegramProperties, telegramBot);
    }

    @Override
    void processKarma(User user) {
    }

    @Override
    public void handle(Message message, String args) {
        Page<UserEntity> userEntityPage =
                userRepository.findAllByOrderByKarmaAsc(PageRequest.of(0, 10));

        logger.debug("Amount of users found in dno10: " + userEntityPage.getTotalElements());

        processTopCommand(userEntityPage, message);
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }
}
