package com.liveintent.weather.weatherservice.model;

import lombok.Data;

@Data
public class Forecast {
    private Coordinates coord;
    private String description;
    private String icon;
    private double minTemp;
    private double maxTemp;
    private long humidity;
    private double windSpeed;
}
