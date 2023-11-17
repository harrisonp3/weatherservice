package com.liveintent.weather.weatherservice.model;

import lombok.Data;

@Data
public class Forecast {
    private double minTemp;
    private double maxTemp;
    private String description;
    private String cityName;
    private String validDate;
}
