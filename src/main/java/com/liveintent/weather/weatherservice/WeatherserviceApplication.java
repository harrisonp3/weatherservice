package com.liveintent.weather.weatherservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.liveintent.weather.weatherservice.model.Forecast;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WeatherserviceApplication {

	private static final Logger log = LoggerFactory.getLogger(WeatherserviceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(WeatherserviceApplication.class, args);
	}

	//@todo hpaup remove this?
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
