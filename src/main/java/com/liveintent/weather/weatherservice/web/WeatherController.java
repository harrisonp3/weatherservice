package com.liveintent.weather.weatherservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import com.liveintent.weather.weatherservice.service.CredentialService;
import com.liveintent.weather.weatherservice.service.WeatherService;
import java.util.Map;
import java.util.Objects;
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
    //@todo abstract this out so it's secure and not hard-coded here
    //private static final String apiKey = "361873f7ccfe4de08d96b649c583eb27";

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private CredentialService credService;

    /**
     * Main endpoint for frontend client code to hit, on success returns populated FullDayForecast model
     * in a ResponseEntity
     *
     * @param multipleParams Map<String, String> should contain "units" AND ("city" OR "lat" & "lon")
     *
     * @return ResponseEntity<FullDayForecast> If fetch is successful, returns FullDayForecast with data
     */
    @GetMapping("/forecast")
    public ResponseEntity<FullDayForecast> getFiveDayForecastByCityOrCoordinates(@RequestParam Map<String, String> multipleParams) {
        try {
            String apiKey = credService.getWeatherbitApiCredential();
            String city = "";
            String lat = "";
            String lon = "";
            // Set a default value for units if it wasn't passed for whatever reason
            String units = "M";
            if (multipleParams.containsKey("units")) {
                units = multipleParams.get("units");
            }
            // If city was passed, don't need to look for coordinate values
            if (multipleParams.containsKey("city")) {
                city = multipleParams.get("city");
            // If coordinates were passed instead, grab them
            } else if (multipleParams.containsKey("lat") && multipleParams.containsKey("lon")) {
                lat = multipleParams.get("lat");
                lon = multipleParams.get("lon");
            }
            else {
                // Didn't pass required query params
                return ResponseEntity.badRequest().build();
            }
            // If "city" is an empty string, use coords instead and call fetchFiveDayForecastByCoords()
            // otherwise, pass "city" into fetchFiveDayForecastByCity()
            FullDayForecast fore = (!Objects.equals(city, "")) ?
                    weatherService.fetchFiveDayForecastByCity(city, apiKey, units) :
                    weatherService.fetchFiveDayForecastByCoords(lat, lon, apiKey, units);
            if (fore == null) {
                // http status 204
                return ResponseEntity.noContent().build();
            }
            ObjectMapper mapper = new ObjectMapper();
            return ResponseEntity.ok().body(fore);
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}
