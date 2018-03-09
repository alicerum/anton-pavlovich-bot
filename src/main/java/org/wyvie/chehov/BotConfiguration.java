package org.wyvie.chehov;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
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

    @Bean("botUser")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public User telegramUser(@Qualifier("telegramBot") TelegramBot telegramBot) {
        GetMe getMe = new GetMe();

        GetMeResponse response = telegramBot.execute(getMe);
        return response.user();
    }
}
