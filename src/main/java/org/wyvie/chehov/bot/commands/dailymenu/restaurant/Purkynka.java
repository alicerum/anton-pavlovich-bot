package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Purkynka extends Restaurant {

    private static final String URL = "http://www.napurkynce.cz/purkynka/denni-menu/";

    private static final String NAME = "purkynka";

    private final Pattern pattern;

    @Autowired
    public Purkynka(UrlHelper urlHelper) {
        super(urlHelper);
        this.pattern = Pattern.compile("<pre>Menu .*?(?=</pre)</pre><pre>(.*?(?=</pre))</pre>");
    }

    @Override
    String getUrl() {
        return Purkynka.URL;
    }

    @Override
    public String getName() {
        return Purkynka.NAME;
    }

    @Override
    String processSource(String source) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            String weekMenu = matcher.group(1);

            if (!StringUtils.isEmpty(weekMenu)) {
                String[] days = weekMenu.split("<br /><br />");

                LocalDateTime today = LocalDateTime.now();
                int dayOfWeek = today.getDayOfWeek().getValue()-1;
                if (dayOfWeek > 4) {
                    result = "Not week day, hence no daily menu. I am very sorry.";
                } else {
                    String menuToday = days[today.getDayOfWeek().getValue() - 1];
                    result = menuToday.replaceAll("<br />", "\n") + "\n";
                }
            }
        }

        return result;
    }

}
