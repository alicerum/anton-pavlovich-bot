package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Kanas extends Restaurant {

    private static final String NAME = "kanas";

    private static final String URL = "http://www.kanas.cz/";

    private final Pattern pattern;

    @Autowired
    public Kanas(UrlHelper urlHelper) {
        super(urlHelper);

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
    public String getName() {
        return Kanas.NAME;
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

                stringBuilder.append(polozka).append("\n\n");
            }
        }

        return stringBuilder.toString().trim();
    }
}
