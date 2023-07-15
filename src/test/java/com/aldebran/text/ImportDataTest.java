package com.aldebran.text;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.MD5;
import com.aldebran.text.ac.AC;
import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextLibManagement;
import com.aldebran.text.similarity.TextSimilaritySearch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ImportDataTest {

    public void importPoems(File poemsFolder, File libsFolder, String libName) throws IOException {
        TextSimilaritySearch lib = new TextSimilaritySearch(
                1.5, 1.5,
                0.55, 2000,
                200, 0.5, 2,
                "poems");

        for (File file : poemsFolder.listFiles()) {
            String title = file.getName().replace(".txt", "");
            String fileContent = FileUtil.readString(file, StandardCharsets.UTF_8);
//            System.out.printf(" title: %s%n content: %s%n", title, fileContent);
            lib.addText(fileContent, title, MD5.create().digestHex(title), 0.5);
        }
        lib.update();
        // 参数重定义
//        lib.changeArgs(2, 2, 0.5,
//                2000, 200, 0.5);
        System.out.println("texts count: " + lib.textsCount());
        TextSimilaritySearch.save(lib, new File(libsFolder, libName));

        System.out.println(lib.similaritySearch("介绍古诗《使至塞上》", 5));

//        System.out.println(lib.similaritySearch("介绍 诗经 蒹葭", 5));
    }

    @Test
    public void tryImportPoems() throws Exception {
        importPoems(new File("C:/user_dir/data/poem"),
                new File("C:/user_dir/data/knowledge_libs"), "poems");
    }

    @Test
    public void tryPoemsLibLoadSearch() throws Exception {
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("C:/user_dir/data/knowledge_libs", "poems"));
        for (SimilaritySearchResult similaritySearchResult :
                lib.similaritySearch("介绍古诗《使至塞上》", 5)) {
            System.out.println(similaritySearchResult);
        }
    }

    public void importWikiJsons(File jsonsFolder, File libsFolder, String libName) throws Exception {
        TextSimilaritySearch lib = new TextSimilaritySearch(
                2, 2,
                0.5, 2000,
                200, 0.5, 2,
                libName);
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
//            if (!segmentS.startsWith("=")) {
//                segmentS = String.format("==基本信息== %s", segmentS);
//                w = 1;
//            }
            String text = title + " " + segmentS;
            String thisId = id + "_" + count;
            lib.addText(text, title, thisId, w);
            count++;
//            System.out.printf("add %s %s %s %n", thisId, title, text);
        }
        return 1;
    }


    @Test
    public void tryImportWikiJsons() throws Exception {
        importWikiJsons(new File("C:/user_dir/data/wiki_data/wiki_interesting_segment"),
                new File("C:/user_dir/data/knowledge_libs"), "wiki_interesting_lib");

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
            mrs = lib.contentAC.indexOf("木卫二欧罗巴直径和质量第四大公转轨道距离木星第六一颗介绍木卫二");
        }
        long searchEd = System.currentTimeMillis();
        System.out.println("search time: " + (searchEd - searchSt) / 1000.0 / times + "s");
        System.out.println(mrs);
    }

    @Test
    public void tryWikiLibLoadSearch() throws Exception {
        long loadSt = System.currentTimeMillis();
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("C:/user_dir/data/knowledge_libs", "wiki_interesting_lib"));
        long loadEd = System.currentTimeMillis();
        System.out.println("load time: " + (loadEd - loadSt) / 1000.0 + "s");

        // 参数重定义
        lib.changeArgs(1, 1, 0.5,
                2000, 200, 0.5);


        // growRate为了处理小IDF的问题

        List<SimilaritySearchResult> resultList = null;
        int times = 20;

        long searchSt = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            resultList = lib.similaritySearch("木卫二(又名欧罗巴)是木星天然卫星中直径和质量第四大，公转轨道距离木星第六近的一颗。" +
                    "介绍木卫二", 15);
//            resultList = lib.similaritySearch("介绍岳飞写的《满江红》(怒发冲冠凭栏处)", 15);
//            resultList = lib.similaritySearch("介绍岳飞写的《满江红写怀》", 15);
//            resultList = lib.similaritySearch("孟浩然的诗 春晓 ", 15);
//            resultList = lib.similaritySearch("介绍苏轼写的念奴娇赤壁怀古", 15);
//            resultList = lib.similaritySearch("春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("孟浩然 春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("金星的质量 ", 10);
        }
        long searchEd = System.currentTimeMillis();
        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }
        System.out.println("search time: " + (searchEd - searchSt) / 1000.0 / times + "s");
        System.out.println("texts count: " + lib.textsCount());
        System.out.println("content words count: " + lib.contentWordsCount());
        System.out.println("grams sum: " + (lib.contentGramsCountSum + lib.titleGramsCountSum));

    }

    @Test
    public void tryManager() throws Exception {
        TextLibManagement textLibManagement = new TextLibManagement(new File("C:/user_dir/data/knowledge_libs"));
        textLibManagement.loadLibFromDisk("wiki_interesting_lib");

        System.out.println(textLibManagement.similaritySearch("wiki_interesting_lib",
                "孟浩然的诗 春晓", 10));
    }
}
