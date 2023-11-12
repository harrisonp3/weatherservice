package com.liveintent.weather.weatherservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveintent.weather.weatherservice.model.Forecast;

import com.liveintent.weather.weatherservice.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Forecast> getForecastByCity(@RequestParam Map<String, String> multipleParams) {
        System.out.println("hit getForecastByCity endpoint");
        try {
            String apiKey = "a4b02892fa24ceb05260687cde51496e";//@todo hpaup refactor
            if (multipleParams.containsKey("city")) {
                String city = multipleParams.get("city");
                Forecast fore = service.findForecastByCity(city, apiKey);
                //return ResponseEntity.ok(fore).getBody();
                HttpHeaders responseHeaders = new HttpHeaders();
                ObjectMapper mapper = new ObjectMapper();
                System.out.println("here is the stringified response: " + mapper.writeValueAsString(fore));
                //return new ResponseEntity<String>(mapper.writeValueAsString(fore), responseHeaders, HttpStatus.OK);
                //System.out.println("hpaup TRYING RESPONSE ENTITY .body().toString()");
                //return fore.toString();

                return ResponseEntity.ok().body(fore); // this is sending a valid network response but still can't access in javascript code
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
        //try {
        //    if (multipleParams.containsKey("lat") && multipleParams.containsKey("lon")) {
        //        double lat = multipleParams.get("lat");
        //        double lon = multipleParams.get("lon");
        //        String apiKey = "a4b02892fa24ceb05260687cde51496e"; //@todo hpaup refactor
        //        //return service.findForecastByCoordinates(lat, lon, apiKey);
        //        service.findForecastByCoordinates2(lat, lon, apiKey);
        //        return null;
        //    } else {
        //        //@todo hpaup error that inputs invalid
        //        System.out.println("lat and/or lon was not present");
        //    }
        //} catch(Exception e) {
        //    System.out.println(e.toString());
        //    return null;
        //}
        System.out.println("hit endpoiont i've temporarily disabled");
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
