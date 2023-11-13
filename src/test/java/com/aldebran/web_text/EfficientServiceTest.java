package com.aldebran.web_text;


import com.aldebran.web_text.bean.GlobalBeans;
import com.aldebran.web_text.entity.EfficientResult;
import com.aldebran.web_text.service.EfficientService;
import com.aldebran.web_text.service.WikiDataSimpleService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

public class EfficientServiceTest {

    EfficientService efficientService = new EfficientService();

    {
        efficientService.globalBeans = new GlobalBeans();
        efficientService.globalBeans.testDataFolder = "D:/user_dir/data/wiki_data/processed_data_segment";
        efficientService.wikiDataSimpleService = new WikiDataSimpleService();
    }

    @Test
    void testEfficientOne() throws Exception {
        EfficientResult efficientResult = efficientService.testEfficientOne(10 * 10000);
        System.out.println(JSON.toJSONString(efficientResult, true));
    }

    @Test
    void efficientResult() throws Exception {
        efficientService.testEfficientMul(60 * 10000, 60 * 10000, 60 * 10000);

    }
}
