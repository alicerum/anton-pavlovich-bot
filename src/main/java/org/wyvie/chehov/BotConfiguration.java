package org.wyvie.chehov;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BotConfiguration {

    private final Logger logger = LoggerFactory.getLogger(BotConfiguration.class);

    @Bean("telegramBot")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TelegramBot telegramBot(@Value("${telegram.api-key}") String telegramApiKey) {
        return new TelegramBot(telegramApiKey);
    }
}
