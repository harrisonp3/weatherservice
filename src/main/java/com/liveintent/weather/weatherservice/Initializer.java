package com.liveintent.weather.weatherservice;

import com.liveintent.weather.weatherservice.model.Event;
import com.liveintent.weather.weatherservice.model.Group;
import com.liveintent.weather.weatherservice.model.GroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

@Component
public class Initializer implements CommandLineRunner {
    private final GroupRepository repository;

    public Initializer(GroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        Stream.of("Seattle JUG", "Denver JUG", "Dublin JUG").forEach(name -> repository.save(new Group(name)));
        Group djug = repository.findByName("Seattle JUG");
        Event e = Event.builder().title("Micro Frontends for Java Developers")
                .description("JHipster now has microtfrontend support!")
                .date("2022-09-10")
                .build();
        djug.setEvents(Collections.singleton(e));
        repository.save(djug);

        repository.findAll().forEach(System.out::println);
    }
}
