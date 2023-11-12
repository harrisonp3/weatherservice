package com.liveintent.weather.weatherservice.service;

import com.liveintent.weather.weatherservice.model.Coordinates;
import com.liveintent.weather.weatherservice.model.Forecast;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
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
            this.parseWeatherApiResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    private void parseWeatherApiResponse(HttpResponse<String> response) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jason = (JSONObject) parser.parse(response.body());
            System.out.println("hpaup here is the json object: ");
            System.out.println(jason);
            System.out.println("visibility is = " + jason.get("visibility")); //ok
            System.out.println(jason.get("main"));// ok
            System.out.println(jason.get("main.humidity"));// bad
            System.out.println(jason.get("humidity"));//bad
            System.out.println(jason.get("coord"));//ok


            JSONObject coords = (JSONObject) jason.get("coord");
            System.out.println("lat is : " + coords.get("lat"));//ok!
            Coordinates coordinates = new Coordinates();
            double lat = (double) coords.get("lat");
            double lon = (double) coords.get("lon");
            coordinates.setLatitude(lat);
            coordinates.setLongitude(lon);

            JSONObject mainBlock = (JSONObject) jason.get("main");
            System.out.println("right before humidity");
            long humidity = (long) mainBlock.get("humidity");
            System.out.println("humidity is " + humidity);
            double maxTemp = (double) mainBlock.get("temp_max");
            System.out.println("maxTemp is " + maxTemp);
            double minTemp = (double) mainBlock.get("temp_min");
            System.out.println("minTemp is " + minTemp);

            JSONArray weatherArrayBlock = (JSONArray) jason.get("weather");
            System.out.println("is there somehow taht i need to index into the array?");
            System.out.println(weatherArrayBlock);
            System.out.println(weatherArrayBlock.get(0));
            //System.out.println(weatherArrayBlock.get("icon"));
            JSONObject weatherBlock = (JSONObject) weatherArrayBlock.get(0);
            System.out.println(weatherBlock);
            System.out.println("icon is = " + weatherBlock.get("icon"));
            String icon = (String) weatherBlock.get("icon");
            String desc = (String) weatherBlock.get("description");
            System.out.println("got past array parse");

            JSONObject windBlock = (JSONObject) jason.get("wind");
            double windSpeed = (double) windBlock.get("speed");

            Forecast fore = new Forecast();
            fore.setHumidity(humidity);
            fore.setCoord(coordinates);
            fore.setIcon(icon);
            fore.setMinTemp(minTemp);
            fore.setMaxTemp(maxTemp);
            fore.setDescription(desc);
            fore.setWindSpeed(windSpeed);
            System.out.println("Here is the Forecast model: ");
            System.out.println(fore);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}


