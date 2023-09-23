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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ImportDataTest {

    public void importPoems(File poemsFolder, File libsFolder, String libName) throws IOException {
        TextSimilaritySearch lib = new TextSimilaritySearch(
                1.5,
                1.5,
                0.5,
                1,
                2,
                8,
                50,
                10,
                2, "poems");

        lib.textPreprocess.loadReplaceMapFromFile("./replace.txt");

        for (File file : poemsFolder.listFiles()) {
            String title = file.getName().replace(".txt", "");
            String fileContent = FileUtil.readString(file, StandardCharsets.UTF_8);
//            System.out.printf(" title: %s%n content: %s%n", title, fileContent);
            lib.addText(fileContent, title, MD5.create().digestHex(title), 1);
        }
        lib.update();
        // 参数重定义
//        lib.changeArgs(1.5, 1.5, 0.5,
//                1, 2, 8, 50, 10);
        System.out.println("texts count: " + lib.textsCount());
        TextSimilaritySearch.save(lib, new File(libsFolder, libName));

//        System.out.println(lib.similaritySearch("介绍古诗《使至塞上》", 5));

        System.out.println(lib.similaritySearch("介绍 诗经 蒹葭", 5));
    }

    @Test
    public void tryImportPoems() throws Exception {
        importPoems(new File("d:/user_dir/data/poem"),
                new File("d:/user_dir/data/knowledge_libs"), "poems");
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
                3,
                2,
                0.5,
                1,
                3,
                8,
                200,
                10,
                2,
                libName);
        lib.textPreprocess.loadReplaceMapFromFile("./replace.txt");
        int c = 0;
        int maxCount = 80 * 10000;

        PriorityQueue<File> queue = new PriorityQueue<>(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.length() < o2.length() ? -1 : 1;
            }
        });

        for (File file : jsonsFolder.listFiles()) {
            queue.add(file);
//            System.out.println(file);
        }

        long t1 = System.currentTimeMillis();

        while (!queue.isEmpty()) {
            File file = queue.remove();
            if (file.getName().endsWith(".json") && !file.getName().startsWith(".")) {
//                System.out.println(c + " " + lib.textsCount());
                c += importJson(file, lib);
            }
            if (lib.textsCount() > maxCount) break;
        }

        long t2 = System.currentTimeMillis();
        lib.update();
        long t3 = System.currentTimeMillis();
        System.out.println("入库文章数量: " + lib.textsCount());
        System.out.println("插入文章所用时间：" + (t2 - t1) / 1000.0);
        System.out.println("刷新索引所用时间：" + (t3 - t2) / 1000.0);
//        System.gc();

        TextSimilaritySearch.save(lib, new File(libsFolder, libName));
        long t4 = System.currentTimeMillis();
        System.out.println("持久化所用时间：" + (t4 - t3) / 1000.0);
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("used memory: " + usedMemory / 1024.0 / 1024.0 / 1024.0 + "G");
    }

    public int importJson(File inFile, TextSimilaritySearch lib) throws Exception {
        String fileContent = FileUtil.readString(inFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(fileContent);
        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        if (title.endsWith("列表")) {
            return 0;
        }
//        System.out.printf("add %s %s %n", id, title);
        int count = 0;
        for (Object segment : jsonObject.getJSONArray("segments")) {
            String segmentS = segment.toString();
            double w = 1;
//            if (!segmentS.startsWith("=")) {
//                segmentS = String.format("==基本信息== %s", segmentS);
//                w = 1;
//            }
            String text = segmentS;
            String thisId = id + "_" + count;
            lib.addText(text, title, thisId, w);
            count++;

//            System.out.printf("add %s %s %s %n", thisId, title, text);
        }
        return 1;
    }


    @Test
    public void tryImportWikiJsons() throws Exception {
        importWikiJsons(new File("D:/user_dir/data/wiki_data/big_data_test_segment"),
                new File("D:/user_dir/data/knowledge_libs"), "big_data_test");

//        importWikiJsons(new File("D:/user_dir/data/wiki_data/wiki_interesting_segment"),
//                new File("D:/user_dir/data/knowledge_libs"), "wiki_interesting_lib");

    }

    @Test
    public void acTest() throws Exception {
        long loadSt = System.currentTimeMillis();
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("D:/user_dir/data/knowledge_libs", "wiki_interesting_lib"));
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
                new File("D:/user_dir/data/knowledge_libs", "big_data_test"));
//        TextSimilaritySearch lib = TextSimilaritySearch.load(
//                new File("D:/user_dir/data/knowledge_libs", "wiki_interesting_lib"));
        long loadEd = System.currentTimeMillis();
        System.out.println("load time: " + (loadEd - loadSt) / 1000.0 + "s");

        // 参数重定义
        lib.changeArgs(3, 2, 0.5,
                1, 3, 8, 200, 10);

        List<SimilaritySearchResult> resultList = null;
        int times = 500;

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
//            resultList = lib.similaritySearch("春晓 春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("金星的质量 ", 30);
        }
        long searchEd = System.currentTimeMillis();
        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }
        System.out.println("相似搜索时间: " + (searchEd - searchSt) / 1000.0 / times + "s");
        System.out.println("statistics: " + lib.getStatistics());
        // 未去重的Grams Count
        System.out.println("avg grams count: " + (lib.contentGramsCountSum + lib.titleGramsCountSum) / lib.textsCount());
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("used memory: " + usedMemory / 1024.0 / 1024.0 / 1024.0 + "G");

    }

    @Test
    public void tryManager() throws Exception {
        TextLibManagement textLibManagement = new TextLibManagement(new File("C:/user_dir/data/knowledge_libs"));
        textLibManagement.loadLibFromDisk("wiki_interesting_lib");

        System.out.println(textLibManagement.similaritySearch("wiki_interesting_lib",
                "孟浩然的诗 春晓", 10));
    }
}
