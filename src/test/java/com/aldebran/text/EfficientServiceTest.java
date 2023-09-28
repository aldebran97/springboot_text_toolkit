package com.aldebran.text;


import com.aldebran.text.bean.LibBeans;
import com.aldebran.text.entity.EfficientResult;
import com.aldebran.text.service.EfficientService;
import com.aldebran.text.service.WikiDataSimpleService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

public class EfficientServiceTest {

    EfficientService efficientService = new EfficientService();

    {
        efficientService.libBeans = new LibBeans();
        efficientService.libBeans.testDataFolder = "D:/user_dir/data/wiki_data/big_data_test_segment";
        efficientService.wikiDataSimpleService = new WikiDataSimpleService();
    }

    @Test
    void testEfficientOne() throws Exception {
        EfficientResult efficientResult = efficientService.testEfficientOne(1 * 10000);
        System.out.println(JSON.toJSONString(efficientResult, true));
    }

    @Test
    void efficientResult() throws Exception {
        efficientService.testEfficientMul(1 * 10000, 2 * 10000);

    }
}
