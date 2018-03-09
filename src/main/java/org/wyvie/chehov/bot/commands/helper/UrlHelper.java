package org.wyvie.chehov.bot.commands.helper;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Service
public class UrlHelper {

    public String getPageSource(String url) throws IOException {
        URL pageUrl = null;
        try {
            pageUrl = new URL(url);
        } catch (MalformedURLException e) {
        }

        if (pageUrl == null)
            return "";

        URLConnection urlConnection = pageUrl.openConnection();
        urlConnection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                        "AppleWebKit/537.11 (KHTML, like Gecko) " +
                        "Chrome/23.0.1271.95 Safari/537.11");

        return IOUtils.toString(urlConnection.getInputStream());
    }
}
