package org.wyvie.chehov.bot.commands.wiki;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WikiCommand implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(WikiCommand.class);

    private static final String COMMAND = "wiki";

    private static final String URL_TEMPLATE="https://ru.wiktionary.org/w/index.php?printable=yes&" +
            "title=%TITLE%";

    private final TelegramBot telegramBot;
    private final User botUser;
    private final UrlHelper urlHelper;

    private final Pattern pattern;

    @Autowired
    public WikiCommand(@Qualifier("telegramBot") TelegramBot telegramBot,
                       @Qualifier("botUser") User botUser,
                       UrlHelper urlHelper) {
        this.telegramBot = telegramBot;
        this.urlHelper = urlHelper;
        this.botUser = botUser;

        this.pattern = Pattern.compile("<span [^>]*id=\"Значение\">.*?(?=<ol>)" +
                "<ol>(.*?(?=</ol>))</ol>");
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public void handle(Message message, String args) {
        if (StringUtils.isEmpty(args)) {
            sendMessage(message.chat().id(),
                    "Пожалуйста, добавьте предмет поиска.\n" +
                            "Пример: /" + COMMAND + "@" + botUser.username() + " собачка");

            return;
        }

        String url = URL_TEMPLATE.replace("%TITLE%", args);

        String source;
        try {
            source = urlHelper.getPageSource(url);
        } catch (FileNotFoundException e) {
            sendMessage(message.chat().id(), "Не нашёл. Отнюдь.");
            return;
        } catch (IOException e) {
            logger.error("io exception in wiki on page: " + url);
            sendMessage(message.chat().id(), "Извините, где-то я напутал и что-то пошло не так.");
            return;
        }

        Matcher matcher = pattern.matcher(source);
        if (!matcher.find()) {
            logger.error("matcher error in wiki page: " + url);
            sendMessage(message.chat().id(), "Не нашёл. Отнюдь.");
            return;
        }

        String definitions = matcher.group(1)
                .replaceAll("(◆&#160;.*?(?=</li>))", "")
                .replaceAll("</li>", "\n\n")
                .replaceAll("(<[^>]*>)", "");
        sendMessage(message.chat().id(), definitions);
    }

    private void sendMessage(long chatId, String errorText) {
        SendMessage sendMessage = new SendMessage(chatId, errorText);
        SendResponse response = telegramBot.execute(sendMessage);
    }
}
