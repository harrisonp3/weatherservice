package com.liveintent.weather.weatherservice.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface GroupRepository extends JpaRepository<Group, Long> {
    //@todo hpaup delete this class
    Group findByName(String name);

    Collection<Group> findAllByUserId(String id);
}
