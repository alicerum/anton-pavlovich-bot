package org.wyvie.chehov.bot.commands.weather;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;
import org.wyvie.chehov.bot.commands.weather.model.WeatherResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class WeatherComand implements CommandHandler {

    private final Logger logger = LoggerFactory.getLogger(WeatherComand.class);

    private static final String COMMAND_NAME = "weather";

    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?id=%CITY_ID%&APPID=%API_KEY%";

    private static final String ICON_URL =
            "http://openweathermap.org/img/w/%ICON_CODE%.png";

    private static final int BRNO_ID = 3078610;

    private final TelegramBot telegramBot;
    private final TelegramProperties telegramProperties;
    private final UrlHelper urlHelper;
    private final Gson gson;

    private WeatherResponse weatherResponse;
    private LocalDateTime lastRetreived;

    @Autowired
    public WeatherComand(TelegramBot telegramBot,
                         TelegramProperties telegramProperties,
                         UrlHelper urlHelper) {
        this.telegramBot = telegramBot;
        this.telegramProperties = telegramProperties;
        this.urlHelper = urlHelper;

        this.gson = new Gson();
    }

    @Override
    public void handle(Message message, String args) {

        WeatherResponse weatherResponse;

        try {
            weatherResponse = getWeatherResponse();
        } catch (IOException e) {
            logger.error("Error during fetch of weather", e);
            sendMessage(message.chat().id(), "Внутренняя ошибка.");
            return;
        }

        StringBuilder result = new StringBuilder("");
        result.append(weatherResponse.getWeather().get(0).getMain()).append(", ")
                    .append(tempToCelcius(weatherResponse.getMain().getTemp())).append("°C")
                    .append("\nmax: ").append(tempToCelcius(weatherResponse.getMain().getTemp_max())).append("°C")
                    .append("\nmin: ").append(tempToCelcius(weatherResponse.getMain().getTemp_min())).append("°C");

        sendMessage(message.chat().id(), result.toString().trim());
    }

    private long tempToCelcius(double tempInFahr) {
        return Math.round(tempInFahr - 273.15);
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }

    private WeatherResponse getWeatherResponse() throws IOException {
        LocalDateTime tenMinutesAgo =
                LocalDateTime.now().minus(Duration.of(10, ChronoUnit.MINUTES));

        if (weatherResponse == null
                || LocalDateTime.now().isBefore(
                        lastRetreived.plus(Duration.of(10, ChronoUnit.MINUTES))
        )) {


            String json = urlHelper.getPageSource(
                    WEATHER_URL.replaceAll("%CITY_ID%", Integer.toString(BRNO_ID))
                            .replaceAll("%API_KEY%", telegramProperties.getWeather().getApiKey()));

            this.weatherResponse = gson.fromJson(json, WeatherResponse.class);
            this.lastRetreived = LocalDateTime.now();
        }

        return weatherResponse;
    }
}
