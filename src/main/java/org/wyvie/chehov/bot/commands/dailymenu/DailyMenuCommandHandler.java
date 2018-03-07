package org.wyvie.chehov.bot.commands.dailymenu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.dailymenu.restaurant.*;

import java.util.HashMap;
import java.util.Map;

public class DailyMenuCommandHandler implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(DailyMenuCommandHandler.class);

    public static final String COMMAND = "dailymenu";

    private TelegramBot telegramBot;

    private Map<String, Restaurant> restaurantMap = new HashMap<>();

    public DailyMenuCommandHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

        restaurantMap.put(Kanas.NAME, new Kanas());
        restaurantMap.put(CookPoint.NAME, new CookPoint());
        restaurantMap.put(Purkynka.NAME, new Purkynka());
        restaurantMap.put(PadThai.NAME, new PadThai());
    }

    @Override
    public void handle(Message message, String args) {
        logger.debug("args is '" + args + "'");

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
}
