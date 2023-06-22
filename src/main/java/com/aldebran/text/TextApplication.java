package com.aldebran.text;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.aldebran.text")
public class TextApplication {

	public static void main(String[] args) {
		SpringApplication.run(TextApplication.class, args);
	}

}
