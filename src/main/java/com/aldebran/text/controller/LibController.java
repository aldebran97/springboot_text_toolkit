package com.aldebran.text.controller;

import com.aldebran.text.similarity.TextLibManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@RestController("/lib")
public class LibController {

    @Autowired
    private TextLibManagement textLibManagement;
}
