package com.liveintent.weather.weatherservice.service;

import com.liveintent.weather.weatherservice.model.Coordinates;
import com.liveintent.weather.weatherservice.model.Forecast;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WeatherService {
    private static final int DAILY_RESULT_LIMIT = 6;
    @Autowired
    private RestTemplate template = new RestTemplate();

    /**public FullDayForecast findForecastByCoordinates(double lat, double lon, String apiKey) {
        return template.getForObject("https://api.openweathermap.org/data/2.5/weather?lat=40.67&lon=73.98&appid=a4b02892fa24ceb05260687cde51496e", FullDayForecast.class);
    }*/


    private FullDayForecast parseWeatherbitForecast(HttpResponse<String> response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject rawForecastResponseObject = (JSONObject) parser.parse(response.body());

        //@todo hpaup consider just storing and returning these as strings and getting rid of all this
        Coordinates coordinates = new Coordinates();

        double latDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, "lat");
        double lonDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, "lon");
        coordinates.setLatitude(latDouble);
        coordinates.setLongitude(lonDouble);

        JSONArray mainData = (JSONArray) rawForecastResponseObject.get("data");
        JSONObject today = (JSONObject) mainData.get(0);
        //@todo hpaup create a safelyExtractNumbmerValueAsLong function
        long humidity = (long) today.get("rh");//@todo hpaup should this be int? api returns int but my code has long so prob need to update
        double maxTemp = this.safelyExtractNumberValueAsDouble(today, "max_temp");
        double minTemp = this.safelyExtractNumberValueAsDouble(today, "min_temp");
        double rightNowTemp = this.safelyExtractNumberValueAsDouble(today,"temp");

        JSONObject weatherBlock = (JSONObject) today.get("weather");
        String icon = (String) weatherBlock.get("icon");
        String desc = (String) weatherBlock.get("description");

        double windSpeed = this.safelyExtractNumberValueAsDouble(today, "wind_spd");

        Forecast[] fiveDayLookahead = new Forecast[5];
        Forecast tomorrow = this.parseIndividualForecast((JSONObject) mainData.get(1));
        Forecast twoDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(2));
        Forecast threeDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(3));
        Forecast fourDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(4));
        Forecast fiveDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(5));

        fiveDayLookahead[0] = tomorrow;
        fiveDayLookahead[1] = twoDaysFromNow;
        fiveDayLookahead[2] = threeDaysFromNow;
        fiveDayLookahead[3] = fourDaysFromNow;
        fiveDayLookahead[4] = fiveDaysFromNow;

        FullDayForecast fore = new FullDayForecast();
        fore.setFiveDayForecast(fiveDayLookahead);
        fore.setHumidity(humidity);
        fore.setCoord(coordinates);
        fore.setIcon(icon);
        fore.setMinTemp(minTemp);
        fore.setMaxTemp(maxTemp);
        fore.setTemp(rightNowTemp);
        fore.setDescription(desc);
        fore.setWindSpeed(windSpeed);
        System.out.println("Here is the Forecast model: ");
        System.out.println(fore);
        return fore;
    }

    private double safelyExtractNumberValueAsDouble(JSONObject rawObject, String key) {
        //@todo hpaup add try catch exception handling
        double temp = 0;
        Object tempRaw = rawObject.get(key);
        if (tempRaw instanceof Double) {
            temp = (double) rawObject.get(key);
        } else if (tempRaw instanceof String) {
            temp = Double.parseDouble((String) tempRaw);
        } else if (tempRaw instanceof Long) {
            temp = Double.parseDouble(Long.toString((Long) tempRaw));
        }
        return temp;
    }

    private Forecast parseIndividualForecast(JSONObject today) {
        JSONObject weatherBlock = (JSONObject) today.get("weather");
        String desc = (String) weatherBlock.get("description");

        double maxTemp = this.safelyExtractNumberValueAsDouble(today, "max_temp");
        double minTemp = this.safelyExtractNumberValueAsDouble(today, "min_temp");

        Forecast forecast = new Forecast();
        forecast.setDescription(desc);
        forecast.setMaxTemp(maxTemp);
        forecast.setMinTemp(minTemp);
        return forecast;
    }

    public FullDayForecast findFiveDayForecastByCity(String city, String apiKey, String units) {
        String openWeatherApiParameterForCityInput = "q";
        String requestUri =
                "https://api.weatherbit.io/v2.0/forecast/daily?" +
                        "city=" +
                        city +
                        "&units=" +
                        units +
                        "&days=" +
                        DAILY_RESULT_LIMIT +
                        "&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            FullDayForecast fore = this.parseWeatherbitForecast(response);
            return fore;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return null;
    }

    //@todo hpaup rename this and other functions to be fetch i think, instead of find
    //@todo hpaup refactor so instead of passing parameters through several layers in the same way, create a
    //@todo hpaup forecast request model or something and make city,apiKey, units attributes of the model so you can pass that
    //@todo hpaup and reuse it in the service for subsequent calls
    public FullDayForecast findFiveDayForecastByCoords(String lat, String lon, String apiKey, String units) {
        String openWeatherApiParameterForCityInput = "q";
        String requestUri =
                "https://api.weatherbit.io/v2.0/forecast/daily?" +
                        "lat=" +
                        lat +
                        "&lon=" +
                        lon +
                        "&units=" +
                        units +
                        "&days=" +
                        DAILY_RESULT_LIMIT +
                        "&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            FullDayForecast fore = this.parseWeatherbitForecast(response);
            return fore;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return null;
    }
}


