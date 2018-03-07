package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PadThai extends Restaurant {

    private static final String URL = "https://www.zomato.com/brno/pad-thai-kr%C3%A1lovo-pole-brno-sever/daily-menu";

    public static final String NAME = "padthai";

    private final Pattern pattern;

    public PadThai() {
        pattern = Pattern.compile("<div class=\"tmi-text-group[^>]*>\\s*" +
                "<div class=\"row\">\\s*" +
                "<div class=\"tmi-name\">(.*?(?=</div))</div>.*?(?=tmi-price)" +
                "tmi-price[^>]*>\\s*" +
                "<div class=\"row\">([^<]*)</div>");
    }

    @Override
    String getUrl() {
        return PadThai.URL;
    }

    @Override
    String processSource(String source) {

        Matcher matcher = pattern.matcher(source);

        StringBuilder tmp = new StringBuilder("");
        for (int i = 0; i < 4; i++) {
            if (matcher.find()) {
                String name = matcher.group(1).trim()
                        .replace("<span class='tmi-qty'>", "")
                        .replace("</span>", "");
                String price = matcher.group(2).trim();

                tmp.append(name).append("\t").append(price).append("\n\n");
            } else {
                tmp = new StringBuilder("");
                break;
            }
        }

        return tmp.toString().trim();
    }

    @Override
    protected String getPageSource(String url) throws IOException {
        java.net.URL pageUrl = null;
        try {
            pageUrl = new URL(url);
        } catch (MalformedURLException e) {
        }

        if (pageUrl == null)
            return "";

        HttpsURLConnection urlConnection = (HttpsURLConnection)pageUrl.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        urlConnection.setRequestProperty("Cookie", "fbcity=93; zl=en;");
        InputStream inputStream = null;
        try {
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream != null ? toString(inputStream) : "";
    }
}
