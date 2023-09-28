package com.aldebran.text.controller;

import com.aldebran.text.service.EfficientService;
import com.aldebran.text.service.WikiDataSimpleService;
import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextLibManagement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/lib")
public class LibController {

//    @Autowired
    private TextLibManagement textLibManagement;

    @Autowired
    EfficientService efficientService;

    public LibController() {
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
    public String test() {
        return "Hello";
    }

    @RequestMapping("/testEfficient")
    public void testEfficient(@RequestParam("unit") int unit, @RequestParam("max") int max) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    efficientService.testEfficientMul(unit, max);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
