package com.liveintent.weather.weatherservice.model;

import lombok.Data;

@Data
public class FullDayForecast extends Forecast {
    private Coordinates coord;
    private String icon;
    private long humidity;
    private double windSpeed;
    private double temp;
    private Forecast[] fiveDayForecast;
}
