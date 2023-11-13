package com.aldebran.web_text.service;

import com.aldebran.text.ac.AC;
import com.aldebran.web_text.bean.GlobalBeans;
import com.aldebran.web_text.entity.EfficientResult;
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
    public GlobalBeans globalBeans;

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
        lib.allowMultiThreadsSearch = true;
        lib.searchDocsUnit = 15000;
        System.out.println(globalBeans.testDataFolder);
        File folder = new File(globalBeans.testDataFolder);

        File[] subFiles = folder.listFiles();
        System.out.println("inserting");
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
        System.out.println("updating");
        long t2 = System.currentTimeMillis();
        lib.update();
        long t3 = System.currentTimeMillis();
        System.out.println("saving");
        File file = new File(libFileName);
        TextSimilaritySearch.save(lib, file, true);
        long t4 = System.currentTimeMillis();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        EfficientResult efficientResult = new EfficientResult();
        efficientResult.textsCount = lib.textsCount();
        efficientResult.diskFileSize = folderSize(file) / 1024.0 / 1024 / 1024;
        efficientResult.takeMemorySize = usedMemory / 1024.0 / 1024 / 1024;
        efficientResult.importTime = (t4 - t1) / 1000.0;
        efficientResult.insertTime = (t2 - t1) / 1000.0;
        efficientResult.updateTime = (t3 - t2) / 1000.0;
        efficientResult.saveTime = (t4 - t3) / 1000.0;

        String query = "木卫二(又名欧罗巴)是木星天然卫星中直径和质量第四大，公转轨道距离木星第六近的一颗。" +
                "介绍木卫二";

        int times = 500;
        List<SimilaritySearchResult> resultList = null;
        long searchSt = System.currentTimeMillis();
        System.out.println("searching, maxDocumentsCount: " + maxDocumentsCount);
        for (int i = 0; i < times; i++) {
            resultList = lib.similaritySearch(query, 10);
        }
        long searchEd = System.currentTimeMillis();
        efficientResult.searchTime = (searchEd - searchSt) * 1.0 / times;

        long acStart = System.currentTimeMillis();
        List<AC.MatchResult> mrs = null;
        for (int i = 0; i < times; i++) {
            mrs = lib.titleAC.indexOf(query);
            mrs = lib.contentAC.indexOf(query);
        }
        long acEnd = System.currentTimeMillis();
        efficientResult.gramCount = lib.gramIdfMap.size();
        efficientResult.acSearchTime = (acEnd - acStart) * 1.0 / times;

        System.out.println(mrs);


        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }

        return efficientResult;
    }

    public void testEfficient2(EfficientResult efficientResult) throws Exception {
        long t1 = System.currentTimeMillis();
        TextSimilaritySearch lib2 = TextSimilaritySearch.load(new File(libFileName), true);
        long t2 = System.currentTimeMillis();
        efficientResult.loadTime = (t2 - t1) / 1000.0;
    }


    public EfficientResult testEfficientOne(int maxDocumentsCount) throws Exception {
        EfficientResult efficientResult = testEfficient1(maxDocumentsCount);
//        testEfficient2(efficientResult);
        return efficientResult;
    }

    public void testEfficientMul(int start, int unit, int max) throws Exception {

        for (int i = start; i <= max; i += unit) {
            EfficientResult efficientResult = testEfficientOne(i);
            System.out.println("efficientResult " + JSON.toJSONString(efficientResult));
        }

    }

    public long folderSize(File folder) {
        if (folder.isFile()) {
            return folder.length();
        } else {
            long all = 0;
            for (File subFile : folder.listFiles()) {
                all += folderSize(subFile);
            }
            return all;
        }
    }
}
