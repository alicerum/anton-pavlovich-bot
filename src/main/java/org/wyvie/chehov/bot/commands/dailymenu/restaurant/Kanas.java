package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kanas implements Restaurant {

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
    public String menu() {

        String pageSource = "";

        try {
            pageSource = URLUtils.getPageSource(URL);
        } catch (IOException ignore) {
        }

        StringBuilder stringBuilder = new StringBuilder("");

        if (!StringUtils.isEmpty(pageSource)) {
            Matcher matcher = pattern.matcher(pageSource);

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
