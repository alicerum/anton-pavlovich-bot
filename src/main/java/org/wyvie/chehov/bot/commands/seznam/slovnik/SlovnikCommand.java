package org.wyvie.chehov.bot.commands.seznam.slovnik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;

/**
 * The type Slovnik command.
 */
@Service
public class SlovnikCommand implements CommandHandler {

   private final Logger logger = LoggerFactory.getLogger(SlovnikCommand.class);

   private static final String COMMAND = "slovnik";
   private static final int MESSAGE_MAX_SIZE = 4096;

   private static final String BASE_SLOVNIK_URL = "https://slovnik.seznam.cz";
   private static final String URL_TEMPLATE = BASE_SLOVNIK_URL + "/%s/?q=%s&shortView=0";

   private static final String MESSAGE_ERROR = "Извините, где-то я напутал и что-то пошло не так.";

   private final TelegramBot telegramBot;
   private final User botUser;
   private final UrlHelper urlHelper;


   @Autowired
   public SlovnikCommand(@Qualifier("telegramBot") TelegramBot telegramBot,
                         @Qualifier("botUser") User botUser,
                         UrlHelper urlHelper) {
      this.telegramBot = telegramBot;
      this.botUser = botUser;
      this.urlHelper = urlHelper;
   }

   @Override
   public void handle(Message message, String args) {
      long messageChatId = message.chat().id();
      if (StringUtils.isEmpty(args.trim())) {
         sendHelpMessage(messageChatId);
         return;
      }

      String langPair;
      String sentence;
      String[] reqParams = args.trim().split(" ");

      List<String> langsList = Arrays
         .stream(reqParams)
         .limit(2)
         .map(p -> p.trim().toUpperCase())
         .filter(p -> LangoPair.getLangPairStartsWith(p) != null || LangoPair.getLangPairEndsWith(p) != null)
         .collect(Collectors.toList());

      if (langsList.isEmpty()) { // default src-targ
         langPair = langoPairToUrlParam(LangoPair.getDefaultLangPair());
         sentence = Arrays.stream(reqParams).collect(Collectors.joining("%20"));
      } else if (langsList.size() == 1 && reqParams.length >= 2) { // only target
         if (LangoPair.getLangPairEndsWith(langsList.get(0)) == null) {
            langPair = reverseLangoPair(langoPairToUrlParam(LangoPair.getLangPairStartsWith(langsList.get(0))));
         } else {
            langPair = langoPairToUrlParam(LangoPair.getLangPairEndsWith(langsList.get(0)));
         }
         sentence = Arrays.stream(reqParams).skip(1).collect(Collectors.joining());
      } else {
         if (LangoPair.getLangPairEndsWith(langsList.get(1)) != null) {// two langs defined
            // by default trying to translate from cz to chosen lang
            // e.g. for pair en-fr will translate cz -> fr
            langPair = langoPairToUrlParam(LangoPair.getLangPairEndsWith(langsList.get(1)));
         } else if (LangoPair.getLangPairStartsWith(langsList.get(1)) != null) {
            langPair = reverseLangoPair(langoPairToUrlParam(LangoPair.getLangPairEndsWith(langsList.get(0))));
         } else {
            langPair = langoPairToUrlParam(LangoPair.getDefaultLangPair());
         }
         sentence = Arrays.stream(reqParams).skip(2).collect(Collectors.joining("%20"));
      }

      String url = String.format(URL_TEMPLATE, langPair, sentence);
      logger.debug("url: " + url);
      String source;
      try {
         source = urlHelper.getPageSourceIgnoreNotFound(url);
         List<List<String>> blocks = parseHtml(source);
         blocks.forEach(b -> {
            StringBuilder sb = new StringBuilder();
            b.forEach(l -> {
               sb.append(l);
               sb.append("   ");
               if (sb.length() > MESSAGE_MAX_SIZE) {
                  sendMessage(messageChatId, sb.toString().trim(), ParseMode.HTML);
                  sb.delete(0, sb.length() - 1);
               }
            });
            sendMessage(messageChatId, sb.toString().trim(), ParseMode.HTML);
         });
      } catch (Exception e) {
         logger.error("Got exception while trying to process slovnik command", e);
         sendMessage(message.chat().id(), MESSAGE_ERROR, null);
      }

   }

   private String langoPairToUrlParam(LangoPair pair) {
      return pair.name().toLowerCase().replace("_", "-");
   }

   private String reverseLangoPair(String pair) {
      String[] langs = pair.split("-");
      return String.format("%s-%s", langs[1], langs[0]);
   }

   @Override
   public String getCommand() {
      return COMMAND;
   }

   private List<List<String>> parseHtml(String source) {
      List<List<String>> blocks = new ArrayList<>();

      Document document = Jsoup.parse(source);
      Elements results = document.select("div#results");

      //#results > div > h1 - subject itself or nothing have been found message
      List<String> headFastDefLine = new ArrayList<>();
      headFastDefLine.add(String.format("<b>%s</b>", results.select("h1").text()));

      results.select("#fastMeanings a").forEach(element -> {
         String hrefNew = BASE_SLOVNIK_URL + element.attr("href");
         element = element.attr("href", hrefNew);
         headFastDefLine.add(element.toString());
      });

      // extended grammatics
      List<String> extDefs = new ArrayList<>();
      results.select("ol li dl").forEach(dl -> {
         Element dt = dl.selectFirst("dt");
         String def = dt.text();
         String aTagsDefs = dt.select("a").stream().map(e -> {
            String hrefNew = BASE_SLOVNIK_URL + e.attr("href");
            return e.attr("href", hrefNew).toString();
         }).collect(Collectors.joining(","));
         extDefs.add(String.format("%s %s", aTagsDefs, def));

         dl.select("dd").forEach(dd -> {
            extDefs.add(dd.wholeText());
         });
      });

      // additional definitions links
      List<String> moreDefs = new ArrayList<>();
      results.select("ul.moreResults li").forEach(li -> {
         Element aTag = li.selectFirst("a");
         li.selectFirst("a").remove();
         String hrefNew = BASE_SLOVNIK_URL + aTag.attr("href");
         aTag = aTag.attr("href", hrefNew);
         String liText = li.text();
         moreDefs.add(String.format("%s <i>%s</i>", aTag, liText));
      });

      // synonyms/antonyms
      List<String> synDefs = new ArrayList<>();
      results.select("div.other-meaning a").forEach(a -> {
         String hrefNew = BASE_SLOVNIK_URL + a.attr("href");
         synDefs.add(a.attr("href", hrefNew).toString());
      });

      // additional definitions links
      List<String> addDefs = new ArrayList<>();
      results.select("#fulltext li").forEach(li -> {
         Element aTag = li.selectFirst("a");
         li.selectFirst("a").remove();
         String hrefNew = BASE_SLOVNIK_URL + aTag.attr("href");
         aTag = aTag.attr("href", hrefNew);
         String liText = li.text();
         addDefs.add(String.format("%s <i>%s</i>", aTag, liText));
      });

      blocks.add(headFastDefLine);
      blocks.add(extDefs);
      blocks.add(synDefs);
      blocks.add(moreDefs);
      blocks.add(addDefs);

      return blocks;
   }

   private void sendHelpMessage(long chatId) {
      String errorMessage = "Пожалуйста, добавьте язык источника (опц.), целевой язык перевода (опц.) и слово (фразу) для перевода.\n" +
         "Доступные языки: en <-> cz, ru <-> cz, de <-> cz, it <-> cz, fr <-> cz, es <-> cz, sk <-> cz.\n" +
         "Пример: /" + COMMAND + "@" + botUser.username() + " en cz pretty girl" + "\n" +
         "или /" + COMMAND + "@" + botUser.username() + " prdel.\n" +
         "Направление перевода по умолчанию: cz -> ru.";
      sendMessage(chatId, errorMessage, null);
   }

   private void sendMessage(long chatId, String messageText, ParseMode parseMode) {
      logger.debug("going to send message: " + messageText + " message size: " + messageText.length());
      SendMessage sendMessage = new SendMessage(chatId, messageText);
      if (parseMode != null) {
         sendMessage = sendMessage.parseMode(parseMode).disableWebPagePreview(true);
      }
      SendResponse response = telegramBot.execute(sendMessage);
   }

   private enum LangoPair {
      CZ_RU,
      CZ_IT,
      CZ_FR,
      CZ_DE,
      CZ_SK,
      CZ_ES,
      CZ_EN;

      private static LangoPair getDefaultLangPair() {
         return CZ_RU;
      }

      private static LangoPair getLangPairStartsWith(String startsWith) {
         LangoPair langPair = getDefaultLangPair();
         if (!StringUtils.isEmpty(startsWith)) {
            langPair = Arrays
               .stream(LangoPair.values())
               .filter(lp -> lp.name().startsWith(startsWith))
               .findAny()
               .orElse(null);
         }
         return langPair;
      }

      private static LangoPair getLangPairEndsWith(String endsWith) {
         LangoPair langPair = getDefaultLangPair();
         if (!StringUtils.isEmpty(endsWith)) {
            langPair = Arrays
               .stream(LangoPair.values())
               .filter(lp -> lp.name().endsWith(endsWith))
               .findAny()
               .orElse(null);
         }
         return langPair;
      }
   }
}
