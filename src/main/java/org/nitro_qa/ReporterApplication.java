package org.nitro_qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class ReporterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReporterApplication.class, args);
    }
}
