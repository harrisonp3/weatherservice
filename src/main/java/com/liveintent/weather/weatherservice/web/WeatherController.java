package com.liveintent.weather.weatherservice.web;

import com.liveintent.weather.weatherservice.model.Forecast;

import com.liveintent.weather.weatherservice.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private WeatherService service;


    //@todo hpaup delete this function
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

    @GetMapping("/forecast/city")
    public Forecast getForecastByCity(@RequestParam Map<String, String> multipleParams) {
        System.out.println("hit getForecastByCity endpoint");
        try {
            String apiKey = "a4b02892fa24ceb05260687cde51496e";//@todo hpaup refactor
            if (multipleParams.containsKey("city")) {
                String city = multipleParams.get("city");
                service.findForecastByCity(city, apiKey);
                return null;
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
    public Forecast getForecastByCoordinates(@RequestParam Map<String, Double> multipleParams) {
        try {
            if (multipleParams.containsKey("lat") && multipleParams.containsKey("lon")) {
                double lat = multipleParams.get("lat");
                double lon = multipleParams.get("lon");
                String apiKey = "a4b02892fa24ceb05260687cde51496e"; //@todo hpaup refactor
                //return service.findForecastByCoordinates(lat, lon, apiKey);
                service.findForecastByCoordinates2(lat, lon, apiKey);
                return null;
            } else {
                //@todo hpaup error that inputs invalid
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        return null;
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
