package com.liveintent.weather.weatherservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherserviceApplication {
	private static final Logger log = LoggerFactory.getLogger(WeatherserviceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(WeatherserviceApplication.class, args);
	}

}
