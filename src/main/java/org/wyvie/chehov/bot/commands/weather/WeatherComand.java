package org.wyvie.chehov.bot.commands.weather;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wyvie.chehov.TelegramProperties;
import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.bot.commands.helper.UrlHelper;
import org.wyvie.chehov.bot.commands.weather.model.WeatherResponse;

import java.io.FileNotFoundException;
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

    private static final String WEATHER_Q_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=%QUERY%&APPID=%API_KEY%";

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
            weatherResponse = getWeatherResponse(args);
        } catch (FileNotFoundException e) {
            sendMessage(message.chat().id(), "Населённый пункт на карте не обнаружен");
            return;
        } catch (IOException e) {
            logger.error("Error during fetch of weather", e);
            sendMessage(message.chat().id(), "Внутренняя ошибка.");
            return;
        }

        StringBuilder result = new StringBuilder("");
        result.append(tempToCelcius(weatherResponse.getMain().getTemp())).append("°C")
                    .append("\nmax: ").append(tempToCelcius(weatherResponse.getMain().getTemp_max())).append("°C")
                    .append("\nmin: ").append(tempToCelcius(weatherResponse.getMain().getTemp_min())).append("°C");

        if (weatherResponse.getRain() != null) {
            result.append("\n\nДождь ").append(weatherResponse.getRain().getAmount() / 3).append("мм");
        }

        if (weatherResponse.getSnow() != null) {
            result.append("\n\nСнег ").append(weatherResponse.getSnow().getAmount() / 3).append("мм");
        }

        if (weatherResponse.getClouds() != null && weatherResponse.getClouds().getAll() > 5) {
            result.append("\n\nОблачность ").append(weatherResponse.getClouds().getAll()).append("%");
        }

        String windDir = metDegreeToDirection(weatherResponse.getWind().getDeg());

        result.append("\n\nВетер")
                .append("\nСкорость ").append(weatherResponse.getWind().getSpeed()).append("м/с")
                .append(windDir != null ? "\nНаправление " + windDir : "");

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

    private WeatherResponse getWeatherResponse(String args) throws IOException {
        if (StringUtils.isEmpty(args))
            return getWeatherResponse();

        String json = urlHelper.getPageSource(
                WEATHER_Q_URL.replaceAll("%QUERY%", args)
                    .replaceAll("%API_KEY%", telegramProperties.getWeather().getApiKey()));

        return gson.fromJson(json, WeatherResponse.class);
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

    private String metDegreeToDirection(double degree) {
        if ((degree >= 348.75 && degree <= 360) || (degree >= 0 && degree < 11.25)) {
            return "С";
        } else if (degree >= 11.25 && degree < 33.75) {
            return "ССВ";
        } else if (degree >= 33.75 && degree < 56.25) {
            return "СВ";
        } else if (degree >= 56.25 && degree < 78.75) {
            return "ВСВ";
        } else if (degree >= 78.75 && degree < 101.25) {
            return "В";
        } else if (degree >= 101.25 && degree < 123.75) {
            return "ВЮВ";
        } else if (degree >= 123.75 && degree < 146.25) {
            return "ЮВ";
        } else if (degree >= 146.25 && degree < 168.75) {
            return "ЮЮВ";
        } else if (degree >= 168.75 && degree < 191.25) {
            return "Ю";
        } else if (degree >= 191.25 && degree < 213.75) {
            return "ЮЮЗ";
        } else if (degree >= 213.75 && degree < 236.25) {
            return "ЮЗ";
        } else if (degree >= 236.25 && degree < 258.75) {
            return "ЗЮЗ";
        } else if (degree >= 258.75 && degree < 281.25) {
            return "З";
        } else if (degree >= 281.25 && degree < 303.75) {
            return "ЗЮЗ";
        } else if (degree >= 303.75 && degree < 326.25) {
            return "СЗ";
        } else if (degree >= 326.25 && degree < 348.75) {
            return "ССЗ";
        }

        return null;
    }
}
