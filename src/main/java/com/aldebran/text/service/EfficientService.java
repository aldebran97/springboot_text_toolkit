package com.aldebran.text.service;

import com.aldebran.text.bean.LibBeans;
import com.aldebran.text.entity.EfficientResult;
import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextSimilaritySearch;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * 效率测试服务
 *
 * @author aldebran
 * @since 2023-09-28
 */
@Service
public class EfficientService {

    @Autowired
    public WikiDataSimpleService wikiDataSimpleService;

    @Autowired
    public LibBeans libBeans;

    public String libFileName = "./test-lib";

    public EfficientResult testEfficient1(int maxDocumentsCount) throws Exception {
        final TextSimilaritySearch lib = new TextSimilaritySearch(
                3,
                2,
                0.5,
                1,
                3,
                8,
                200,
                10,
                2,
                "test");
        lib.textPreprocess.loadReplaceMapFromFile("./replace.txt");
        System.out.println(libBeans.testDataFolder);
        File folder = new File(libBeans.testDataFolder);

        File[] subFiles = folder.listFiles();
        long t1 = System.currentTimeMillis();
        for (File file : subFiles) {
            if (file.getName().endsWith(".json") && !file.getName().startsWith(".")) {
                wikiDataSimpleService.dealJsonSplit(file, new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) {
                        lib.addText(strings.get(2), strings.get(1), strings.get(0), 1);
                    }
                }, 205);
            }
            if (lib.textsCount() >= maxDocumentsCount) break;
        }
        long t2 = System.currentTimeMillis();
        lib.update();
        long t3 = System.currentTimeMillis();
        File file = new File(libFileName);
        TextSimilaritySearch.save(lib, file);
        long t4 = System.currentTimeMillis();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        EfficientResult efficientResult = new EfficientResult();
        efficientResult.textsCount = lib.textsCount();
        efficientResult.diskFileSize = file.length() / 1024.0 / 1024 / 1024;
        efficientResult.takeMemorySize = usedMemory / 1024.0 / 1024 / 1024;
        efficientResult.importTime = (t4 - t1) / 1000.0;
        efficientResult.insertTime = (t2 - t1) / 1000.0;
        efficientResult.updateTime = (t3 - t2) / 1000.0;
        efficientResult.saveTime = (t4 - t3) / 1000.0;

        int times = 500;

        List<SimilaritySearchResult> resultList = null;
        long searchSt = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            resultList = lib.similaritySearch("木卫二(又名欧罗巴)是木星天然卫星中直径和质量第四大，公转轨道距离木星第六近的一颗。" +
                    "介绍木卫二", 10);
        }
        long searchEd = System.currentTimeMillis();
        efficientResult.searchTime = (searchEd - searchSt) * 1.0 / times;

        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }

        return efficientResult;
    }

    public void testEfficient2(EfficientResult efficientResult) throws Exception {
        long t1 = System.currentTimeMillis();
        TextSimilaritySearch lib2 = TextSimilaritySearch.load(new File(libFileName));
        long t2 = System.currentTimeMillis();
        efficientResult.loadTime = (t2 - t1) / 1000.0;
    }


    public EfficientResult testEfficientOne(int maxDocumentsCount) throws Exception {
        EfficientResult efficientResult = testEfficient1(maxDocumentsCount);
        testEfficient2(efficientResult);
        return efficientResult;
    }

    public void testEfficientMul(int unit, int max) throws Exception {

        for (int i = unit; i <= max; i += unit) {
            EfficientResult efficientResult = testEfficientOne(i);
            System.out.println("efficientResult " + JSON.toJSONString(efficientResult));
        }

    }
}
