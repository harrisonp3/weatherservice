package com.liveintent.weather.weatherservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import com.liveintent.weather.weatherservice.service.WeatherService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(WeatherController.class);
    private static final String apiKey = "361873f7ccfe4de08d96b649c583eb27";

    @Autowired
    private WeatherService service;

    @GetMapping("/forecast")
    public ResponseEntity<FullDayForecast> getFiveDayForecastByCityOrCoordinates(@RequestParam Map<String, String> multipleParams) {
        try {
            // Set a default value for units if it wasn't passed for whatever reason
            String units = "M";
            if (multipleParams.containsKey("units")) {
                units = multipleParams.get("units");
            }
            // If city was passed, call service method that uses city name
            if (multipleParams.containsKey("city")) {
                String city = multipleParams.get("city");
                FullDayForecast fore = service.fetchFiveDayForecastByCity(city, apiKey, units);
                if (fore == null) {
                    // http status 204
                    return ResponseEntity.noContent().build();
                }
                ObjectMapper mapper = new ObjectMapper();
                System.out.println("here is the stringified response: " + mapper.writeValueAsString(fore));
                return ResponseEntity.ok().body(fore);
                // If coordinates were passed instead, call service method that uses coords
            } else if (multipleParams.containsKey("lat") && multipleParams.containsKey("lon")) {
                String lat = multipleParams.get("lat");
                String lon = multipleParams.get("lon");
                FullDayForecast fore = service.fetchFiveDayForecastByCoords(lat, lon, apiKey, units);
                if (fore == null) {
                    // http status 204
                    return ResponseEntity.noContent().build();
                }
                ObjectMapper mapper = new ObjectMapper();
                System.out.println("here is the stringified response: " + mapper.writeValueAsString(fore));
                return ResponseEntity.ok().body(fore);
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}
