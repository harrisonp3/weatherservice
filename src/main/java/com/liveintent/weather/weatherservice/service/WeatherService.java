package com.liveintent.weather.weatherservice.service;

import com.liveintent.weather.weatherservice.model.Forecast;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WeatherService {
    @Autowired
    private RestTemplate template = new RestTemplate();

    public Forecast findForecastByCoordinates(double lat, double lon, String apiKey) {
        return template.getForObject("https://api.openweathermap.org/data/2.5/weather?lat=40.67&lon=73.98&appid=a4b02892fa24ceb05260687cde51496e", Forecast.class);
    }

    public void findForecastByCoordinates2(double lat, double lon, String apiKey) {
        System.out.println("IN THE findForecastByCoordinates2() METHOD");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat=40.67&lon=73.98&appid=a4b02892fa24ceb05260687cde51496e"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONParser parser = new JSONParser();
            JSONObject jason = (JSONObject) parser.parse(response.body());
            System.out.println("hpaup here is the json object: ");
            System.out.println(jason);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }
}


