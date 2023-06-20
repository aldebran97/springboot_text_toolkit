package com.aldebran.text.bean;

import com.aldebran.text.similarity.TextLibManagement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class LibBeans {

    @Value("text.libsFolder")
    public String libsFolder;

    @Bean
    public TextLibManagement textLibManagement() {
        return new TextLibManagement(new File(libsFolder));
    }
}
