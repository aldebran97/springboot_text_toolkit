package com.aldebran.text.bean;

import com.aldebran.text.similarity.TextLibManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;

@Configuration
public class LibBeans {

    @Value("${text.libsFolder}")
    public String libsFolder;

    @Value("${text.libNames}")
    public String libNames;

    private static Logger logger = LoggerFactory.getLogger(LibBeans.class);

    @Bean
    public TextLibManagement textLibManagement() throws Exception {
        TextLibManagement textLibManagement = new TextLibManagement(new File(libsFolder));
        textLibManagement.loadLibsFromDisk(Arrays.asList(libNames.split(",")));
        logger.info("loaded {}", Arrays.asList(libNames.split(",")));
        return textLibManagement;
    }
}
