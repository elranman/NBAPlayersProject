package com.test.nba.nbaInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class NbaInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbaInfoApplication.class, args);
	}

}
