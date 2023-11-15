package com.liveintent.weather.weatherservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import com.liveintent.weather.weatherservice.service.WeatherService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private WeatherService service;

    //@todo hpaup rename endpooint
    @GetMapping("/forecast/hpaup")
    public ResponseEntity<FullDayForecast> getFiveDayForecastByCity(@RequestParam Map<String, String> multipleParams) {
        System.out.println("hit getFiveDayForecastByCity endpoint");
        try {
            String apiKey = "361873f7ccfe4de08d96b649c583eb27";//@todo hpaup refactor
            String units = "M";//@todo hpaup make sure M,S,I are the options in the frontend picklist for units
            if (multipleParams.containsKey("units")) {
                units = multipleParams.get("units");
            }
            if (multipleParams.containsKey("city")) {
                String city = multipleParams.get("city");
                FullDayForecast fore = service.findFiveDayForecastByCity(city, apiKey, units);
                HttpHeaders responseHeaders = new HttpHeaders();
                ObjectMapper mapper = new ObjectMapper();
                System.out.println("here is the stringified response: " + mapper.writeValueAsString(fore));
                return ResponseEntity.ok().body(fore);
            } else {
                //@todo hpaup error that inputs invalid
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        return null;
    }
    @GetMapping("/forecast/coords")
    public FullDayForecast getForecastByCoordinates(@RequestParam Map<String, Double> multipleParams) {
        return null;
    }
}
