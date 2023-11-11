package com.liveintent.weather.weatherservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    //@todo hpaup delete this class
    @Id
    private String id;
    private String name;
    private String email;
}
