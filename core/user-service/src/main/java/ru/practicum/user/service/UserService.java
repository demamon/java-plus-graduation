package ru.practicum.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("ru.practicum")
public class UserService {
    public static void main(String[] args) {
        SpringApplication.run(UserService.class, args);
    }
}
