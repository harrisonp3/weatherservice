package com.liveintent.weather.weatherservice.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast, String> {
    Optional<Forecast> findByCity(String city);
}
