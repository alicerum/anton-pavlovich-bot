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
import org.springframework.util.StringUtils;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.UserRepository;

@Service
public class Top10Command extends AbstractKarmaCommand {

    private final Logger logger = LoggerFactory.getLogger(Top10Command.class);

    private static final String COMMAND_NAME = "top10";

    private static final String ERROR_NO_USERS = "Не знаю никаких пользователей";

    @Autowired
    public Top10Command(UserRepository userRepository,
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
                userRepository.findAllByOrderByKarmaDesc(PageRequest.of(0, 10));

        logger.debug("Amount of users found in top10: " + userEntityPage.getTotalElements());

        if (userEntityPage.getTotalElements() == 0) {
            sendMessage(message.chat().id(), ERROR_NO_USERS);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder("");
        userEntityPage.forEach(userEntity -> {
            String username = userEntity.getUsername();
            if (StringUtils.isEmpty(username)) {
                username = (userEntity.getFirstName() + " " + userEntity.getLastName()).trim();
            }

            stringBuilder.append(username).append(": ").append(userEntity.getKarma()).append("\n");
        });

        sendMessage(message.chat().id(), stringBuilder.toString().trim());
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }
}
