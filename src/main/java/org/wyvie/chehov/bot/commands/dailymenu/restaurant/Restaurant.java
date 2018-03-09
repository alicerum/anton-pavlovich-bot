package org.wyvie.chehov.bot.commands.dailymenu.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Service
public abstract class Restaurant {

    abstract String getUrl();
    abstract String processSource(String source);

    public abstract String getName();

    private final UrlHelper urlHelper;

    @Autowired
    public Restaurant(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public String menu() {
        String pageSource = "";

        try {
            pageSource = urlHelper.getPageSource(getUrl());
        } catch (IOException ignore) {
        }

        return processSource(pageSource);

    }


}
