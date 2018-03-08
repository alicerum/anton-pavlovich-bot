package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.stereotype.Service;

@Service
public class PadThai extends Restaurant {

    private static final String URL = "http://rhlp.skutka.cz/PadThai";

    private static final String NAME = "padthai";

    @Override
    String getUrl() {
        return PadThai.URL;
    }

    @Override
    public String getName() {
        return PadThai.NAME;
    }

    @Override
    String processSource(String source) {
        return source.replaceAll("<table>", "")
                .replaceAll("</table>", "")
                .replaceAll("</td><td>", "\t")
                .replaceAll("</tr><tr>", "\n\n")
                .replaceAll("<tr><td>", "")
                .replaceAll("</td></tr>", "")
                .replaceAll("<td>", "")
                .replaceAll("</td>", "")
                .trim();
    }
}
