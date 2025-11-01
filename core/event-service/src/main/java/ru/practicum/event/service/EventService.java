package ru.practicum.event.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum"})
@EnableDiscoveryClient
@EnableFeignClients("ru.practicum")
public class EventService {
	public static void main(String[] args) {
		SpringApplication.run(EventService.class, args);
	}

}
