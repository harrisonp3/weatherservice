package com.liveintent.weather.weatherservice.service;

import com.liveintent.weather.weatherservice.model.Forecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Autowired
    private RestTemplate template = new RestTemplate();

    public Forecast findForecastByCoordinates(long lat, long lon, String apiKey) {
        return template.getForObject("https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}", Forecast.class);
    }
}
