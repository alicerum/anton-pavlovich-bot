package org.wyvie.chehov.bot.commands.seznam.slovnik;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Coll2;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.FtxSamp;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Grp;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Head;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Other;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Relations;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Samp2;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Sen;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.SlovnikJSON;
import org.wyvie.chehov.bot.commands.seznam.slovnik.api.json.Translate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Slovnik command.
 */
@Service
public class SlovnikCommand implements CommandHandler {

    private static final String COMMAND = "slovnik";
    private static final int MESSAGE_MAX_SIZE = 4096;
    private static final String BASE_SLOVNIK_API_URL = "https://slovnik.seznam.cz/api/slovnik";
    private static final String BASE_SLOVNIK_FRONT_URL_TEMPLATE = "https://slovnik.seznam.cz/preklad/%s/%s";
    private static final String API_URL_SEGMENT_TEMPLATE = "?dictionary=%s&query=%s"; // /preklad/from_to/subj
    private static final String API_URL_TEMPLATE = BASE_SLOVNIK_API_URL + API_URL_SEGMENT_TEMPLATE;
    private static final String EMPTY_RESULT_MESSAGE = "Nothing. Ничего. Nic.";
    private static final String MESSAGE_ERROR = "Call for help, I'm screwed up.";
    private final Logger logger = LoggerFactory.getLogger(SlovnikCommand.class);
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

        if (langsList.isEmpty()) { // default src_targ
            langPair = langoPairToUrlParam(LangoPair.getDefaultLangPair());
            sentence = String.join("%20", reqParams);
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

        try {
            String url = String.format(API_URL_TEMPLATE, langPair, urlHelper.urlEncode(sentence));
            logger.debug("url: " + url);
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");

            String source = urlHelper.getPageSource(url, headers);
            logger.debug("JSON:" + source);

            List<List<String>> blocks = parseJSON(source);
            blocks.forEach(b -> {
                StringBuilder sb = new StringBuilder();
                b.forEach(l -> {
                    sb.append(l);
                    sb.append("\n");
                    if (sb.length() > MESSAGE_MAX_SIZE) {
                        sendMessage(messageChatId, sb.toString().trim(), ParseMode.HTML);
                        sb.delete(0, sb.length() - 1);
                    }
                });
                sendMessage(messageChatId, sb.toString().trim(), ParseMode.HTML);
            });
        } catch (IOException ioe) {
            logger.error("Something wrong with remote site:", ioe);
            sendMessage(message.chat().id(), MESSAGE_ERROR, null);
        } catch (Exception e) {
            logger.error("Got exception while trying to process slovnik command", e);
            sendMessage(message.chat().id(), MESSAGE_ERROR, null);
        }

    }

    private List<List<String>> parseJSON(String source) {
        List<List<String>> blocks = new ArrayList<>();
        Gson gson = new Gson();
        SlovnikJSON slovnikJSON = gson.fromJson(source, SlovnikJSON.class);

        List<Translate> translates = slovnikJSON.getTranslate();
        if (translates != null && !translates.isEmpty()) {
            translates.forEach(translate -> {
                List<String> mainSections = new ArrayList<>();
                Head head = translate.getHead();
                String header = getBoldText(head.getEntr());
                String morf = getItalicText(head.getMorf());
                mainSections.add(header);
                mainSections.add(morf);

                List<Grp> grps = translate.getGrps();
                grps.forEach(grp -> {
                    List<Sen> sens = grp.getSens();
                    sens.forEach(sen -> {
                        StringBuilder genSb = new StringBuilder();
                        String num = sen.getNumb();
                        StringBuilder tranSb = new StringBuilder();
                        if (!StringUtils.isEmpty(num)) {
                            genSb.append(num);
                            genSb.append(". ");
                        }
                        String phrs = sen.getPhrs();
                        if (!StringUtils.isEmpty(phrs)) {
                            tranSb.append(phrs);
                            tranSb.append(" ");
                            tranSb.append("\u2192");
                            tranSb.append(" ");
                        }

                        List<List<String>> trans = sen.getTrans();
                        trans.forEach(tran -> tran.forEach(tranSb::append));
                        genSb.append(removeHTMLTags(tranSb.toString()));
                        genSb.append("\n");

                        List<Samp2> samp2s = sen.getSamp2();
                        samp2s.forEach(samp2 -> {
                            genSb.append(removeHTMLTags(samp2.getSamp2s()));
                            genSb.append(" ");
                            genSb.append("\u2192");
                            genSb.append(" ");
                            genSb.append(removeHTMLTags(samp2.getSamp2t()));
                            genSb.append("\n");
                        });

                        List<Coll2> coll2s = sen.getColl2();
                        coll2s.forEach(coll2 -> {
                            genSb.append(removeHTMLTags(coll2.getColl2s()));
                            genSb.append(" ");
                            genSb.append("\u2192");
                            genSb.append(" ");
                            genSb.append(removeHTMLTags(coll2.getColl2t()));
                            genSb.append("\n");
                        });
                        mainSections.add(getPreText(genSb.toString().trim()));

                    });
                });
                blocks.add(mainSections);
            });
        }

        Relations relations = slovnikJSON.getRelations();
        if (relations != null) {
            String dict = relations.getDict();

            List<String> odvozenaSlova = relations.getOdvozenaSlova();
            if (odvozenaSlova != null) {
                blocks.add(renderAdditionalSection("Odvozená slova", odvozenaSlova, dict));
            }

            List<String> slovniSpojeni = relations.getSlovniSpojen();
            if (slovniSpojeni != null) {
                blocks.add(renderAdditionalSection("Slovní spojení", slovniSpojeni, dict));
            }

            List<String> synonyms = relations.getSynonyma();
            if (synonyms != null) {
                blocks.add(renderAdditionalSection("Synonyma", synonyms, dict));
            }

            List<String> antonyms = relations.getAntonyma();
            if (antonyms != null) {
                blocks.add(renderAdditionalSection("Antonyma", antonyms, dict));
            }

            List<String> predpony = relations.getPredpony();
            if (predpony != null) {
                blocks.add(renderAdditionalSection("Předpony", predpony, dict));
            }

            List<FtxSamp> ftxSamps = slovnikJSON.getFtxSamp();
            if (ftxSamps != null && !ftxSamps.isEmpty()) {
                List<String> ftxSampSection = new ArrayList<>();
                StringBuilder ftxSampSb = new StringBuilder();
                ftxSampSb.append(getItalicText("Vyskytuje se v"));
                ftxSampSb.append("\n");
                final String[] currentDict = {dict};
                if (dict == null) {
                    currentDict[0] = LangoPair.getDefaultLangPair().name().toLowerCase();
                }
                ftxSamps.forEach(ftxSamp -> {
                    try {
                        if (ftxSamp.getReve() > 0) {
                            currentDict[0] = reverseLangoPair(currentDict[0]);
                        }
                        ftxSampSb.append(getATagText(currentDict[0], removeHTMLTags(ftxSamp.getEntr())));
                    } catch (UnsupportedEncodingException e) {
                        ftxSampSb.append(removeHTMLTags(ftxSamp.getEntr()));
                    }
                    ftxSampSb.append(" ");
                    ftxSampSb.append("\u2192");
                    ftxSampSb.append(" ");
                    ftxSampSb.append(removeHTMLTags(ftxSamp.getSamp2s()));
                    ftxSampSb.append(" ");
                    ftxSampSb.append(removeHTMLTags(ftxSamp.getSamp2t()));
                    ftxSampSb.append("\n");
                });
                ftxSampSection.add(ftxSampSb.toString());
                blocks.add(ftxSampSection);
            }

            List<Other> others = slovnikJSON.getOther();
            List<String> othersSection = new ArrayList<>();
            StringBuilder oSb = new StringBuilder();
            if (others != null && !others.isEmpty()) {
                others.forEach(other -> {
                    try {
                        oSb.append(getATagText(other.getDict(), removeHTMLTags(other.getEntr())));
                    } catch (UnsupportedEncodingException e) {
                        oSb.append(removeHTMLTags(other.getEntr()));
                    }
                    oSb.append(" ");
                    oSb.append("\u2192");
                    oSb.append(" ");
                    oSb.append(removeHTMLTags(other.getTrans()));
                    oSb.append("\n");
                });
                othersSection.add(oSb.toString());
                blocks.add(othersSection);
            }
        }

        if (blocks.stream().allMatch(List::isEmpty)) {
            List<String> emptyResult = new ArrayList<>();
            emptyResult.add(EMPTY_RESULT_MESSAGE);
            blocks.add(emptyResult);
        }
        return blocks;
    }

    private List<String> renderAdditionalSection(String sectionHeader, List<String> sectionList, String dict) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> section = new ArrayList<>();
        stringBuilder.append(getItalicText(sectionHeader));
        stringBuilder.append("\n");
        sectionList.forEach(s -> {
            try {
                stringBuilder.append(getATagText(dict, removeHTMLTags(s)));
            } catch (UnsupportedEncodingException e) {
                stringBuilder.append(removeHTMLTags(s));
            }
            stringBuilder.append("\n");
        });
        section.add(stringBuilder.toString());
        return section;
    }

    private String removeHTMLTags(String text) {
        return Jsoup.parse(text).text();
    }

    private String getBoldText(String text) {
        return String.format("<b>%s</b>", text);
    }

    private String getItalicText(String text) {
        return String.format("<i>%s</i>", text);
    }

    private String getPreText(String text) {
        return String.format("<pre>%s</pre>", text);
    }

    private String getATagText(String langPair, String text) throws UnsupportedEncodingException {
        if (!langPair.startsWith("cz")) {
            langPair = reverseLangoPair(langPair);
        }
        return String.format("<a href=\"%s\">%s</a>",
                String.format(
                        BASE_SLOVNIK_FRONT_URL_TEMPLATE,
                        LangoPair.getLangPairForName(langPair).getPairFrontName(),
                        urlHelper.urlEncode(text)),
                text);
    }

    private String langoPairToUrlParam(LangoPair pair) {
        return pair.name().toLowerCase();
    }

    private String reverseLangoPair(String pair) {
        String[] langs = pair.split("_");
        return String.format("%s_%s", langs[1], langs[0]);
    }

    @Override
    public String getCommand() {
        return COMMAND;
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
        CZ_RU("cesky_rusky"),
        CZ_IT("cesky_italsky"),
        CZ_FR("cesky_francouzksy"),
        CZ_DE("cesky_nemecky"),
        CZ_SK("cesky_slovensky"),
        CZ_ES("cesky_spanelsky"),
        CZ_EN("cesky_anglicky");

        private String pairFrontName;

        LangoPair(String pairNameDirect) {
            this.pairFrontName = pairNameDirect;
        }

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

        private static LangoPair getLangPairForName(String pairName) {
            LangoPair langPair = getDefaultLangPair();
            if (!StringUtils.isEmpty(pairName)) {
                langPair = Arrays
                        .stream(LangoPair.values())
                        .filter(lp -> lp.name().equalsIgnoreCase(pairName))
                        .findAny()
                        .orElse(null);
            }
            return langPair;
        }

        public String getPairFrontName() {
            return pairFrontName;
        }
    }
}
