package org.wyvie.chehov;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegram")
public class TelegramProperties {

    private String apiKey;
    private int updateLimit;

    private boolean debug;

    private Karma karma;

    private Weather weather;

    private String timeZone;

    private String bannedUsers;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getUpdateLimit() {
        return updateLimit;
    }

    public void setUpdateLimit(int updateLimit) {
        this.updateLimit = updateLimit;
    }

    public Karma getKarma() {
        return karma;
    }

    public void setKarma(Karma karma) {
        this.karma = karma;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(String bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public static class Karma {
        private int updateDelay;

        public int getUpdateDelay() {
            return updateDelay;
        }

        public void setUpdateDelay(int updateDelay) {
            this.updateDelay = updateDelay;
        }
    }

    public static class Weather {
        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
