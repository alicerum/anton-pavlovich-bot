package org.wyvie.chehov.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.UserRepository;

import java.util.Optional;

@Service
public class SlapCommand implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(SlapCommand.class);

    private final static String MESSAGE = "%USER1% slaps %USER2% around a bit with a %OBJECT%";
    private final static String DEFAULT_OBJECT = "large trout";

    private final static String COMMAND_NAME = "slap";

    private final TelegramBot telegramBot;
    private final UserRepository userRepository;

    public SlapCommand(@Qualifier("telegramBot")TelegramBot telegramBot,
                       UserRepository userRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Message message, String args) {

        String username = null;
        String object = DEFAULT_OBJECT;
        Message replied = message.replyToMessage();
        if (replied != null) {
            username = toUsername(replied.from());
            if (StringUtils.hasLength(args))
                object = args;
        } else if (StringUtils.hasLength(args)) {

            String argun = args;
            if (argun.startsWith("@"))
                argun = argun.substring(1);

            if (argun.contains(" ")) {
                int position = argun.indexOf(' ');
                object = argun.substring(position + 1);
                argun = argun.substring(0, position);
            }

            Optional<UserEntity> userEntity = userRepository.findByUsername(argun);
            if (userEntity.isPresent())
                username = "@" + argun;
        }

        if (username == null) {
            logger.debug("Could not find username");
            sendMessage(message.chat().id(),
                    MESSAGE.replaceAll("%USER1%", toUsername(message.from()))
                           .replaceAll("%USER2%", "themselves")
                           .replaceAll("%OBJECT%", object));

            return;
        }

        sendMessage(message.chat().id(),
                MESSAGE.replaceAll("%USER1%", toUsername(message.from()))
                       .replaceAll("%USER2%", username)
                       .replaceAll("%OBJECT%", object));
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    private String toUsername(User user) {
        String userName = user.username();

        if (userName == null || "".equals(userName.trim())) {
            String firstName = user.firstName() == null ? "" : user.firstName();
            String lastName = user.lastName() == null ? "" : user.lastName();
            userName = (firstName + " " + lastName);
        } else
            userName = "@" + userName;

        return userName;
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }
}
