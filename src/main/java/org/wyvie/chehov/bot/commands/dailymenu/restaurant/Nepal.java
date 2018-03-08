package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Nepal extends Restaurant {

    private static final String MENU_URL = "http://nepalbrno.cz/weekly-menu/";

    private static final String NAME = "nepal";

    private final Pattern pattern;


    public Nepal() {
        pattern = Pattern.compile("<tr>\\s*<td>(.*?(?=</td))</td><td><strong>(.*?(?=</strong))</strong></td>\\s*</tr>");
    }

    @Override
    String getUrl() {
        return MENU_URL;
    }

    @Override
    public String getName() {
        return Nepal.NAME;
    }

    @Override
    String processSource(String source) {
        Matcher matcher = pattern.matcher(source);

        List<String> list = new ArrayList<>();

        while (matcher.find()) {
            String name = matcher.group(1).replace("<br />", "");
            String price = matcher.group(2);

            list.add(name + "\t" + price);
        }

        StringBuilder result = new StringBuilder("");
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue()-1;
        for (int i = dayOfWeek*5; i < dayOfWeek*5+5; i++) {
            result.append(list.get(i)).append("\n\n");
        }

        return result.toString().trim();
    }
}
