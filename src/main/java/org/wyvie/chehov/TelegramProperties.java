package org.wyvie.chehov;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegram")
public class TelegramProperties {

    private String apiKey;
    private int updateLimit;

    private Karma karma;

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

    public static class Karma {
        private int updateDelay;

        public int getUpdateDelay() {
            return updateDelay;
        }

        public void setUpdateDelay(int updateDelay) {
            this.updateDelay = updateDelay;
        }
    }
}
