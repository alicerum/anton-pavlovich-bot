package org.wyvie.chehov;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(TelegramProperties.class)
public class BotConfiguration {

    private final Logger logger = LoggerFactory.getLogger(BotConfiguration.class);

    private TelegramProperties telegramProperties;

    @Autowired
    public BotConfiguration(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @Bean("telegramBot")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TelegramBot telegramBot() {
        return new TelegramBot(telegramProperties.getApiKey());
    }
}
