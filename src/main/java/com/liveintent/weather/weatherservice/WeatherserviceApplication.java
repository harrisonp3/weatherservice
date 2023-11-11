package com.liveintent.weather.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherserviceApplication.class, args);
		//@todo hpaup delete below this
		Weather app = new Weather();
		app.demo();
	}

}
