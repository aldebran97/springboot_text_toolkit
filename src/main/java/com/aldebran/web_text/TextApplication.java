package com.aldebran.web_text;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.aldebran.web_text")
public class TextApplication {

	public static void main(String[] args) {
		SpringApplication.run(TextApplication.class, args);
	}

}
