package org.wyvie.chehov.bot.commands.reminder;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.SendResponse;
import okio.Buffer;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.annotations.PublicChatOnlyCommand;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@Service
@PublicChatOnlyCommand
class AbstractRemindCommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRemindCommand.class);
    static final String USAGE = "Usage: \n" +
            "\t/remind <YYYY-MM-DDThh:mm:ss> [reminder text] - set reminder for group at given time.\n" +
            "\t/remind list - list all reminders set for group\n" +
            "\t/remind cancel [id] - cancel all or reminder with id provided.";
    static final String DATE_FORMAT_ERROR = "Please, use date format: <b>YYYY-MM-DDThh:mm:ss</b>";
    static final String DEFAULT_REMINDER_TEXT = " You called my name and I ran out of that grave ...";
    private final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(20));
    Map<String, Reminder> scheduledNotifications = new ConcurrentHashMap<>();

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    User botUser;
    @Autowired
    private TelegramProperties telegramProperties;

    // Get arbitrary chat member for arbitrary chat
    ChatMember getChatMember(Chat chat, User user) {
        GetChatMember getChatMemberRequest = new GetChatMember(chat.id(), user.id());
        GetChatMemberResponse getChatMemberResponse = telegramBot.execute(getChatMemberRequest);
        ChatMember chatMember = getChatMemberResponse.chatMember();
        logger.debug("*** ChatMember: " + chatMember);
        return chatMember;
    }

    SendResponse sendMessage(Object id, String messageText, @Nullable ParseMode parseMode) {
        String chatId;
        if (id instanceof User) {
            chatId = ((User) id).id().toString();
        } else if (id instanceof Chat) {
            chatId = ((Chat) id).id().toString();
        } else {
            logger.error("!!! Unknown type of id.");
            return null;
        }

        logger.debug(">>> Sending message: " + messageText + " message size: " + messageText.length() + " to chat id: " + chatId);
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        if (parseMode != null) {
            sendMessage = sendMessage.parseMode(parseMode).disableWebPagePreview(true);
        }
        return telegramBot.execute(sendMessage);
    }

    List<String> parseCommandArgs(String stringArgs) {
        return Arrays.asList(StringUtils.tokenizeToStringArray(stringArgs, " "));
    }

    String sha256(String string) {
        Buffer sink = new Buffer();
        ByteString bs = sink.writeString(string, Charset.defaultCharset()).sha256();
        return bs.hex();
    }

    long parseDateTimeToEpoch(String date) {
        ZoneId zoneId = ZoneId.of(telegramProperties.getTimeZone());
        LocalDateTime notificationDateTime = LocalDateTime.parse(date);
        return notificationDateTime.atZone(zoneId).toEpochSecond() * 1000;
    }

    ScheduledFuture scheduleReminder(Runnable reminderTask, long when) {
        return taskScheduler.schedule(reminderTask, new Date(when));
    }

    String getRemindersList(User user, Chat chat) {
        StringBuilder sb = new StringBuilder();
        scheduledNotifications.forEach((s, reminder) -> {
            if (reminder.getUser().id().equals(user.id()) && reminder.getChat().id().equals(chat.id())) {
                sb
                        .append("<b>")
                        .append(s, 0, 4)
                        .append("</b>")
                        .append(" - ")
                        .append(reminder.getDate())
                        .append(" - ")
                        .append(reminder.getText())
                        .append("\n");
            }
        });

        if (sb.length() == 0) {
            sb
                    .append("No reminders left for chat ")
                    .append("<b>")
                    .append(chat.title())
                    .append("</b>");
        }

        return sb.toString();
    }

    void cancelReminder(User user, Chat chat, @Nullable String id) {
        scheduledNotifications.forEach((s, reminder) -> {
            if (reminder.getUser().id().equals(user.id()) && reminder.getChat().id().equals(chat.id())) {
                ScheduledFuture task = reminder.getTask();
                if (id != null && !task.isCancelled() && !task.isDone()) {
                    if (id.length() < 4) {
                        return;
                    }
                    if (s.startsWith(id) || s.endsWith(id)) {
                        task.cancel(true);
                    }
                } else {
                    task.cancel(true);
                }
                sendMessage(user, "Reminder <b>" + s.substring(0, 4) + "</b> cancelled.", ParseMode.HTML);
                cleanFinishedTaskReferences();
                logger.debug("*** Reminder: " + s + " cancelled.");
            }
        });
    }

    @Scheduled(fixedDelay = 60000)
    private void cleanFinishedTaskReferences() {
        scheduledNotifications.forEach((s, reminder) -> {
            if (reminder.getTask().isDone() || reminder.getTask().isCancelled()) {
                scheduledNotifications.remove(s);
                logger.debug("*** Reminder: " + s + " has been cleaned up.");
            }
        });
    }
}
