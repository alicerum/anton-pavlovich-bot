package org.wyvie.chehov.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.CommandProcessor;
import org.wyvie.chehov.bot.commands.helper.EmojiHelper;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageReader {

    private final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private final CommandProcessor commandProcessor;
    private final TelegramBot telegramBot;
    private final TelegramProperties telegramProperties;
    private final User botUser;
    private final UserRepository userRepository;
    private final EmojiHelper emojiHelper;

    private int lastOffset;

    @Autowired
    public MessageReader(CommandProcessor commandProcessor,
                         TelegramProperties telegramProperties,
                         @Qualifier("telegramBot") TelegramBot telegramBot,
                         @Qualifier("botUser") User botUser,
                         UserRepository userRepository,
                         EmojiHelper emojiHelper) {

        this.commandProcessor = commandProcessor;
        this.telegramBot = telegramBot;
        this.telegramProperties = telegramProperties;
        this.botUser = botUser;
        this.userRepository = userRepository;
        this.emojiHelper = emojiHelper;

        this.lastOffset = 0;
    }

    @Scheduled(fixedDelay = 200)
    public void readMessages() {
        GetUpdates getUpdates = new GetUpdates()
                .limit(telegramProperties.getUpdateLimit())
                .offset(lastOffset)
                .timeout(0);

        GetUpdatesResponse response = telegramBot.execute(getUpdates);
        List<Update> updates = response.updates();

        updates.forEach(update -> {
            lastOffset = update.updateId() + 1;

            Message message = update.message();

            if (message != null && message.text() != null) {
                logger.debug("Got message '" + message.text() + "' from chat_id " + message.chat().id());

                persistUser(message.from());

                if (validateCommmand(message))
                    commandProcessor.processCommand(message);
                else {
                    String messageText = message.text().trim();
                    if (messageText.startsWith("+") || messageText.startsWith("-")
                            || emojiHelper.isThumbsUp(messageText)
                            || emojiHelper.isThumbsDown(messageText))
                        commandProcessor.processKarma(message);
                }

                lastOffset = update.updateId() + 1;
            }
        });
    }

    /**
     * Validates this is the command we would like to process
     * @param message message from Telegram chat
     * @return true if we want to process the command, false otherwise
     */
    private boolean validateCommmand(Message message) {
        String messageText = message.text();

        // if starts with '/' symbol, it's a command
        if (!StringUtils.isEmpty(messageText) &&
                messageText.startsWith("/")) {

            messageText = messageText.toLowerCase().trim();

            String command = messageText.contains(" ") ?
                    messageText.split(" ")[0] : messageText;

            // talking to another bot here
            if (command.contains("@") &&
                    !command.endsWith("@" + botUser.username())) {

                return false;
            }

            return true;
        }

        // not a command
        return false;
    }

    private void persistUser(User user) {
        Integer userId = user.id();
        if (userId != null) {
            UserEntity userEntity = userRepository
                    .findById(userId)
                    .orElseGet(UserEntity::new);

            userEntity.setId(userId);
            userEntity.setUsername(user.username());
            userEntity.setLastSeen(LocalDateTime.now());
            userEntity.setAllowed(true);
            userEntity.setFirstName(user.firstName());
            userEntity.setLastName(user.lastName());

            userRepository.save(userEntity);
            userRepository.flush();
        }
    }
}
