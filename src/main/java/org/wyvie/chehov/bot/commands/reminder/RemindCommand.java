package org.wyvie.chehov.bot.commands.reminder;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wyvie.chehov.bot.commands.CommandHandler;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component
public class RemindCommand extends AbstractRemindCommand implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(RemindCommand.class);
    private static final String COMMAND = "remind";
    private static final String MAKE_BOT_ADMIN = "Bot must be an admin with a <b>'pin messages'</b> or <b>'edit messages'</b> permissions.";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public void handle(Message message, String args) {

        logger.debug("chat: " + message.chat().toString() + " from user");

        Chat chat = message.chat();

        if (!userCanPinEdit(botUser, chat)) {
            sendMessage(chat, MAKE_BOT_ADMIN, ParseMode.HTML);
            return;
        }

        User user = message.from();

        List<String> tokenizedArgs = parseCommandArgs(args);
        logger.debug("*** Tokenized arguments are: " + tokenizedArgs);

        if (tokenizedArgs.isEmpty()) {
            this.sendMessage(chat, USAGE, null);
            return;
        }

        // list all notifications for this user in this chat
        if (tokenizedArgs.get(0).equalsIgnoreCase("list")) {
            sendMessage(user, getRemindersList(user, chat), ParseMode.HTML);
            return;
        }

        // cancel task by id or cancel all tasks
        if (tokenizedArgs.get(0).equalsIgnoreCase("cancel")) {
            String id = tokenizedArgs.size() > 1 ? tokenizedArgs.get(1) : null;
            cancelReminder(user, chat, id);
            sendMessage(user, getRemindersList(user, chat), ParseMode.HTML);
            return;
        }

        // continue with setting up reminder
        long when;
        try {
            when = parseDateTimeToEpoch(tokenizedArgs.get(0));
        } catch (DateTimeParseException e) {
            sendMessage(chat, DATE_FORMAT_ERROR, ParseMode.HTML);
            return;
        }

        String reminderText = tokenizedArgs.size() < 2 ? DEFAULT_REMINDER_TEXT : tokenizedArgs.stream().skip(1).collect(Collectors.joining(" "));

        ScheduledFuture scheduledReminder = scheduleReminder(() -> {
            long messageChatId = chat.id();
            SendResponse response = sendMessage(chat, reminderText, null);
            logger.debug("<<< Send Response: " + response.toString());
            int messageToPinId = response.message().messageId();
            PinChatMessage pinChatMessage = new PinChatMessage(messageChatId, messageToPinId);
            BaseResponse pin = telegramBot.execute(pinChatMessage);
            logger.debug("<<< Pin Response: " + pin.toString());
        }, when);

        // keep scheduled task reference for list or cancel
        Reminder reminder = new Reminder(user, chat, tokenizedArgs.get(0), reminderText, scheduledReminder);
        String key = sha256(args + scheduledReminder.toString());
        scheduledNotifications.put(key, reminder);
        sendMessage(chat, "Reminder created.", null);
        logger.debug("*** Reminders list size: " + scheduledNotifications.size() + ", last one id: " + key + " set by user: " + user);
    }

    // Bot user must be user with rights to pin messages (for groups) or edit messages (for channels).
    private boolean userCanPinEdit(User user, Chat chat) {
        ChatMember member = getChatMember(chat, user);
        boolean canPin = Optional.ofNullable(member.canPinMessages()).orElse(false) ;
        boolean canEdit = Optional.ofNullable(member.canEditMessages()).orElse(false) ;
        return canPin || canEdit;
    }
}