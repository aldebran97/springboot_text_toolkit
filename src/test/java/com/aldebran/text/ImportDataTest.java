package com.aldebran.text;

import cn.hutool.core.io.FileUtil;
import com.aldebran.text.ac.AC;
import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextLibManagement;
import com.aldebran.text.similarity.TextSimilaritySearch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ImportDataTest {

    public int importJson(File inFile, TextSimilaritySearch lib) throws Exception {
        String fileContent = FileUtil.readString(inFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(fileContent);
        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        if (title.endsWith("列表")) {
            return 0;
        }
        System.out.printf("add %s %s %n", id, title);
        int count = 0;
        for (Object segment : jsonObject.getJSONArray("segments")) {
            String segmentS = segment.toString();
            double w = 0.5;
            if (!segmentS.startsWith("=")) {
                segmentS = String.format("==基本信息== %s", segmentS);
                w = 1;
            }
            String text = title + " " + segmentS;
            String thisId = id + "_" + count;
            lib.addText(text, title, thisId, w);
            count++;
//            System.out.printf("add %s %s %s %n", thisId, title, text);
        }
        return 1;
    }


    public void importWikiJsons(File jsonsFolder, File libsFolder, String libName) throws Exception {
        TextSimilaritySearch lib = new TextSimilaritySearch(
                libName, 1, 1,
                0.5, 2, 20, 20, 0.9);
        int c = 0;
        for (File file : jsonsFolder.listFiles()) {
            if (file.getName().endsWith(".json") && !file.getName().startsWith(".")) {
                System.out.println(c);
                c += importJson(file, lib);
            }
        }
        lib.update();
        System.out.println("入库文章数量: " + lib.textsCount());

        TextSimilaritySearch.save(lib, new File(libsFolder, libName));
    }


    // -Xss
    @Test
    public void tryImport() throws Exception {
        importWikiJsons(new File("/Users/aldebran/custom/data/wiki/wiki_interesting_segment"),
                new File("/Users/aldebran/custom/data/wiki/libs"), "wiki_interesting_lib");

    }

    @Test
    public void tryLoadSearch() throws Exception {
        long loadSt = System.currentTimeMillis();
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("/Users/aldebran/custom/data/wiki/libs", "wiki_interesting_lib"));
        long loadEd = System.currentTimeMillis();
        System.out.println("load time: " + (loadEd - loadSt) / 1000.0 + "s");

        lib.regenerateArgs(1, 1,
                0.5, 20, 20, 0.9);

        // growRate为了处理小IDF的问题

        List<SimilaritySearchResult> resultList = null;
        int times = 1;

        long searchSt = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
//            resultList = lib.similaritySearch("木卫二(又名欧罗巴)是木星天然卫星中直径和质量第四大，公转轨道距离木星第六近的一颗。" +
//                    "介绍木卫二", 30);
//            resultList = lib.similaritySearch("介绍岳飞写的《满江红》(怒发冲冠凭栏处)", 10);
//            resultList = lib.similaritySearch("孟浩然的诗 春晓 ", 10);
//            resultList = lib.similaritySearch("介绍苏轼写的念奴娇赤壁怀古", 10);
            resultList = lib.similaritySearch("春眠不觉晓 处处闻啼鸟 ", 10);
        }
        long searchEd = System.currentTimeMillis();
        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }
        System.out.println("search time: " + (searchEd - searchSt) / 1000.0 / times + "s");
        System.out.println("texts count: " + lib.textsCount());
        System.out.println("grams count: " + lib.wordsCount());

    }

    @Test
    public void tryManager() throws Exception {
        TextLibManagement textLibManagement = new TextLibManagement(new File("/Users/aldebran/custom/data/wiki/libs"));
        textLibManagement.loadLibFromDisk("wiki_interesting_lib");

        System.out.println(textLibManagement.similaritySearch("wiki_interesting_lib",
                "孟浩然的诗 春晓", 10));
    }

    @Test
    public void acTest() throws Exception {
        long loadSt = System.currentTimeMillis();
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("/Users/aldebran/custom/data/wiki/libs", "wiki_jsons1_lib"));
        long loadEd = System.currentTimeMillis();
        System.out.println("load time: " + (loadEd - loadSt) / 1000.0 + "s");
        List<AC.MatchResult> mrs = null;
        int times = 20;
        long searchSt = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            mrs = lib.ac.indexOf("木卫二欧罗巴直径和质量第四大公转轨道距离木星第六一颗介绍木卫二");
        }
        long searchEd = System.currentTimeMillis();
        System.out.println("search time: " + (searchEd - searchSt) / 1000.0 / times + "s");
        System.out.println(mrs);
    }
}
