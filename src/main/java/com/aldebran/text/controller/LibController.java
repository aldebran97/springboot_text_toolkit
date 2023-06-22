package com.aldebran.text.controller;

import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextLibManagement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/lib")
public class LibController {

    @Autowired
    private TextLibManagement textLibManagement;

    public LibController(){
        System.out.println("LibController!!!");
    }

    @RequestMapping(value = "/similaritySearch", method = {RequestMethod.POST})
    public List<SimilaritySearchResult> similaritySearch(
            @RequestBody String reqBody
    ) {
        JSONObject reqBodyJson = JSON.parseObject(reqBody);

        List<String> libNames = new ArrayList<>();
        for (Object libName : reqBodyJson.getJSONArray("libNames")) {
            libNames.add((String) libName);
        }

        return textLibManagement.similaritySearch(libNames,
                reqBodyJson.getString("text"),
                reqBodyJson.getInteger("topK"));
    }

    @RequestMapping("/test")
    public String test(){
        return  "Hello";
    }
}
