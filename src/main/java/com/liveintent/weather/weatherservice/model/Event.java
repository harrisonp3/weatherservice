package com.liveintent.weather.weatherservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Event {
    @Id
    @GeneratedValue
    private Long id;
    private String date;
    private String title;
    private String description;
    @ManyToMany
    private Set<User> attendees;
}
