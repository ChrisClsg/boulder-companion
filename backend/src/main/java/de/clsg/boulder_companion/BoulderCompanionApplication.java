package de.clsg.boulder_companion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import de.clsg.boulder_companion.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class BoulderCompanionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoulderCompanionApplication.class, args);
	}

}
