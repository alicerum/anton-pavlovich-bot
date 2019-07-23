package org.wyvie.chehov.bot.commands.helper;

import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class UrlHelper {

    public String getPageSource(String url) throws IOException {
        return getPage(url, null);
    }

    public String getPageSource(String url, Map<String, String> headers) throws IOException{
        return getPage(url, headers);
    }

    private static String toString(InputStream inputStream) throws IOException
    {
        try (
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
        ) {

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }

    public String getPageSourceIgnoreNotFound(String url) throws IOException {
        URL pageUrl = null;
        try {
            pageUrl = new URL(url);
        } catch (MalformedURLException e) {
        }

        if (pageUrl == null)
            return "";

        HttpsURLConnection urlConnection = (HttpsURLConnection)pageUrl.openConnection();
        urlConnection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                        "AppleWebKit/537.11 (KHTML, like Gecko) " +
                        "Chrome/23.0.1271.95 Safari/537.11");

        InputStream is = null;
        try {
            is = urlConnection.getInputStream();
        } catch (FileNotFoundException ignore) {
            if (urlConnection.getResponseCode() == 404)
                is = urlConnection.getErrorStream();
        }

        if (is == null)
            return "";

        return toString(is);
    }

    public String urlEncode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
    }

    public String urlDecode(String text) throws UnsupportedEncodingException {
        return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
    }


    private String getPage(String url, Map<String, String> headers) throws IOException {
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
        if (!headers.isEmpty()) {
            headers.forEach(urlConnection::setRequestProperty);
        }
        return toString(urlConnection.getInputStream());
    }
}
