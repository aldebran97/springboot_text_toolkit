package com.aldebran.text.bean;

import com.aldebran.text.similarity.TextLibManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

@Configuration
public class LibBeans {

    @Value("${text.libsFolder}")
    public String libsFolder;

    @Value("${text.libNames}")
    public String libNames;

    @Value("${test.dataFolder}")
    public String testDataFolder;

    private static Logger logger = LoggerFactory.getLogger(LibBeans.class);

//    @Bean
    public TextLibManagement textLibManagement() throws Exception {
        logger.info("loading {}", Arrays.asList(libNames.split(",")));
        TextLibManagement textLibManagement = new TextLibManagement(new File(libsFolder));
        textLibManagement.loadLibsFromDisk(Arrays.asList(libNames.split(",")));
        logger.info("loaded {}", Arrays.asList(libNames.split(",")));
        return textLibManagement;

    }
}
