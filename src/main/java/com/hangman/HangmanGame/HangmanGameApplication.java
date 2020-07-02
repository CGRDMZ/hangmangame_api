package com.hangman.HangmanGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class HangmanGameApplication {


	public static void main(String[] args) {
		SpringApplication.run(HangmanGameApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowCredentials(true).allowedOrigins("http://localhost:8081").allowedOrigins("http://192.168.1.2:8081").allowedOrigins("https://cgrdmz.github.io");
			}
		};
	}

}
