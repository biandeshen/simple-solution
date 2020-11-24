package xyz.biandeshen.simpleeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class SimpleEurekaApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleEurekaApplication.class, args);
	}
	
}
