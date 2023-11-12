package com.liveintent.weather.weatherservice.web;

import com.liveintent.weather.weatherservice.model.Forecast;
import com.liveintent.weather.weatherservice.model.ForecastRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(GroupController.class);
    private ForecastRepository forecastRepository;

    public WeatherController(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }
    @GetMapping("/forecast/{city}")
    ResponseEntity<?> getForecast(@PathVariable String city) {
        Optional<Forecast> forecast = forecastRepository.findByCity(city);
        return forecast.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * @GetMapping("/group/{id}")
     *     ResponseEntity<?> getGroup(@PathVariable Long id) {
     *         Optional<Group> group = groupRepository.findById(id);
     *         return group.map(response -> ResponseEntity.ok().body(response))
     *                 .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
     *     }
     */
}
