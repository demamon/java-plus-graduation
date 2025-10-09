package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm", "ru.practicum.client"})
@EnableDiscoveryClient
@EnableFeignClients("ru.practicum.client")
public class EWMServer {
	public static void main(String[] args) {
		SpringApplication.run(EWMServer.class, args);
	}

}
