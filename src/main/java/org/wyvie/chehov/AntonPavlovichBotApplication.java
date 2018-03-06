package org.wyvie.chehov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AntonPavlovichBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntonPavlovichBotApplication.class, args);
    }
}
