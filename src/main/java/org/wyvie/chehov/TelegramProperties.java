package org.wyvie.chehov;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegram")
public class TelegramProperties {

    private String apiKey;
    private int updateLimit;

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
}
