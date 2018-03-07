package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kanas extends Restaurant {

    public static final String NAME = "kanas";

    private static final String URL = "http://www.kanas.cz/";

    private final Pattern pattern;

    public Kanas() {
        pattern = Pattern.compile("<div class=\"polozka\">\\s*" +
                "<span class=\"mnozstvi\">([^<]*)</span>\\s*" +
                "<span class=\"jidlo\">([^<]*)</span>\\s*" +
                "<span class=\"cena\">([^<]*)</span>");
    }

    @Override
    String getUrl() {
        return URL;
    }

    @Override
    String processSource(String source) {
        StringBuilder stringBuilder = new StringBuilder("");

        if (!StringUtils.isEmpty(source)) {
            Matcher matcher = pattern.matcher(source);

            while (matcher.find()) {
                String mnozstvi = matcher.group(1);
                String jidlo = matcher.group(2);
                String cena = matcher.group(3);

                String polozka =
                        (StringUtils.isEmpty(mnozstvi) ? "" : mnozstvi + " ") +
                                jidlo + " " +
                                cena;

                stringBuilder.append(polozka).append("\n");
            }
        }

        return stringBuilder.toString().trim();
    }
}
