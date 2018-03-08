package org.wyvie.chehov.bot.commands.dailymenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.dailymenu.restaurant.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyMenuCommandHandler implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(DailyMenuCommandHandler.class);

    private static final String COMMAND = "dailymenu";

    private TelegramBot telegramBot;

    private Map<String, Restaurant> restaurantMap = new HashMap<>();

    @Autowired
    public DailyMenuCommandHandler(List<Restaurant> restaurants,
                                   TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

        restaurants.forEach(restaurant ->
                restaurantMap.put(restaurant.getName(), restaurant));
    }

    @Override
    public void handle(Message message, String args) {
        logger.debug("args is '" + args + "'");

        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        if (dayOfWeek > 5) {
            SendMessage sendMessage = new SendMessage(message.chat().id(),
                    "Only available on week days.");
            telegramBot.execute(sendMessage);
            return;
        }

        Restaurant restaurant = restaurantMap.get(args);
        String textToSend;
        if (restaurant == null) {
            textToSend = "Please specify one of the following restaurants:\n";
            StringBuilder sb = new StringBuilder("");
            restaurantMap.forEach((k, v) -> {
                 if (sb.length() > 0) sb.append(", ");
                 sb.append(k);
            });
            textToSend += sb.toString();
        } else {
           textToSend = restaurant.menu();

           if (StringUtils.isEmpty(textToSend))
               textToSend = "Something went wrong. I could not fetch menu for you." +
                       "I am so very, very sorry. :'(";
        }

        SendMessage sendMessage = new SendMessage(message.chat().id(), textToSend);
        telegramBot.execute(sendMessage);
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
