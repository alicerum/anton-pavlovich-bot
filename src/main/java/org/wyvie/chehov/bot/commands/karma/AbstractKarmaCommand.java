package org.wyvie.chehov.bot.commands.karma;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public abstract class AbstractKarmaCommand implements CommandHandler {

    private static final String ERROR_TOO_EARLY = "Защита от накрутки!";
    private static final String ERROR_NOT_REPLY = "Команда должна вызываться в качестве ответа на другое сообщение";
    private static final String ERROR_YOURSELF = "Лайкать себя некрасиво";
    private static final String INFO_NEW_KARMA = "Готово! Теперь у %USER% карма %KARMA%";

    final UserRepository userRepository;
    private final TelegramProperties telegramProperties;
    private final TelegramBot telegramBot;

    AbstractKarmaCommand(UserRepository userRepository,
                         TelegramProperties telegramProperties,
                         TelegramBot telegramBot) {
        this.userRepository = userRepository;
        this.telegramProperties = telegramProperties;
        this.telegramBot = telegramBot;
    }

    abstract void processKarma(User user);

    private int getUserKarma(int userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        return userEntity.map(UserEntity::getKarma).orElse(0);
    }

    void addToKarma(User telegramUser, int amountToAdd) {
        Optional<UserEntity> userEntity = userRepository.findById(telegramUser.id());
        UserEntity user;
        if (userEntity.isPresent()) {
            user = userEntity.get();
            user.setKarma(user.getKarma() + amountToAdd);
        } else {
            user = createUser(telegramUser);
            user.setKarma(amountToAdd);
        }
        userRepository.save(user);
    }

    protected LocalDateTime lastSetKarma(int userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        return userEntity.map(UserEntity::getLastSetKarma).orElse(
                LocalDateTime.MIN);
    }

    private void updateLastSetKarma(int userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastSetKarma(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    private boolean canUserUpdateNow(int userId) {
        int updateDelay = telegramProperties.getKarma().getUpdateDelay();

        LocalDateTime lastUserSetKarma =
                userRepository.findById(userId)
                        .map(UserEntity::getLastSetKarma)
                        .orElse(LocalDateTime.MIN);

        LocalDateTime lastGood = LocalDateTime.now().minus(Duration.of(updateDelay, ChronoUnit.SECONDS));
        return lastUserSetKarma.isBefore(lastGood);
    }

    void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }

    void processCommand(Message message) {
        int userId = message.from().id();

        Message replied = message.replyToMessage();
        if (replied == null) {
            sendMessage(message.chat().id(), ERROR_NOT_REPLY);
            return;
        }

        if (message.from().id().equals(replied.from().id())) {
            sendMessage(message.chat().id(), ERROR_YOURSELF);
            return;
        }

        if (!canUserUpdateNow(userId)) {
            sendMessage(message.chat().id(), ERROR_TOO_EARLY);
            return;
        }

        processKarma(replied.from());

        updateLastSetKarma(message.from().id());

        int newKarma = getUserKarma(replied.from().id());

        String username = replied.from().username();
        if (username == null || "".equals(username.trim()))
            username = (replied.from().firstName() + " " + replied.from().lastName()).trim();
        else
            username = "@" + username.trim();

        sendMessage(message.chat().id(),
                INFO_NEW_KARMA.replaceAll("%USER%", username)
                        .replaceAll("%KARMA%", Integer.toString(newKarma)));

    }

    private UserEntity createUser(User telegramUser) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(telegramUser.id());
        userEntity.setUsername(telegramUser.username());
        userEntity.setFirstName(telegramUser.firstName());
        userEntity.setLastName(telegramUser.lastName());
        userEntity.setAllowed(true);
        userEntity.setLastSeen(LocalDateTime.now());

        return userEntity;
    }
}