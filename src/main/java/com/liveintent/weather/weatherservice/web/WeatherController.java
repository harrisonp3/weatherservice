package com.liveintent.weather.weatherservice.web;

import com.liveintent.weather.weatherservice.model.Forecast;

import com.liveintent.weather.weatherservice.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private WeatherService service;


    @GetMapping("/hpaup/{dummy}")
    public boolean dummyFrontendBackendConnectionMethod(@PathVariable String dummy) {
        try {
            System.out.println(dummy);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @GetMapping("/forecast/{lat}/{lon}")
    public Forecast getForecastTest(@PathVariable double lat, @PathVariable double lon) {
        try {
            //@todo hpaup remove these explicit declarations
            //lat=40.67;
            //lon=73.98;
            System.out.println(lat);
            System.out.println(lon);
            String apiKey = "a4b02892fa24ceb05260687cde51496e";
            //return service.findForecastByCoordinates(lat, lon, apiKey);
             service.findForecastByCoordinates2(lat, lon, apiKey);
             return null;
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**@GetMapping("/forecast/{city}")
    ResponseEntity<?> getForecast(@PathVariable String city) {
        Optional<Forecast> forecast = forecastRepository.findByCity(city);
        return forecast.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }*/

    /**
     * @GetMapping("/group/{id}")
     *     ResponseEntity<?> getGroup(@PathVariable Long id) {
     *         Optional<Group> group = groupRepository.findById(id);
     *         return group.map(response -> ResponseEntity.ok().body(response))
     *                 .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
     *     }
     */
}
