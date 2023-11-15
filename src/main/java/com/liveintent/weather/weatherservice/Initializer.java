package com.liveintent.weather.weatherservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    @Override
    public void run(String... strings) {
        System.out.println("running mainline from Initializer.java class");
        /**Stream.of("Seattle JUG", "Denver JUG", "Dublin JUG").forEach(name -> repository.save(new Group(name)));
        Group djug = repository.findByName("Seattle JUG");
        Event e = Event.builder().title("Micro Frontends for Java Developers")
                .description("JHipster now has microtfrontend support!")
                .date("2022-09-10")
                .build();
        djug.setEvents(Collections.singleton(e));
        repository.save(djug);

        repository.findAll().forEach(System.out::println);*/
    }
}
