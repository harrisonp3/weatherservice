package com.liveintent.weather.weatherservice.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
