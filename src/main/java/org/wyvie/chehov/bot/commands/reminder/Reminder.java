package org.wyvie.chehov.bot.commands.reminder;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.concurrent.ScheduledFuture;

class Reminder {
    private User user;
    private Chat chat;
    private String date;
    private String text;
    private ScheduledFuture task;

    Reminder(User user, Chat chat, String date, String text, ScheduledFuture task) {
        this.user = user;
        this.chat = chat;
        this.date = date;
        this.text = text;
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    String getDate() {
        return date;
    }

    String getText() {
        return text;
    }

    ScheduledFuture getTask() {
        return task;
    }
}
