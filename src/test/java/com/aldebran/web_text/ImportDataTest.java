package com.aldebran.web_text;

import com.aldebran.text.ac.AC;
import com.aldebran.web_text.service.WikiDataSimpleService;
import com.aldebran.text.similarity.SimilaritySearchResult;
import com.aldebran.text.similarity.TextLibManagement;
import com.aldebran.text.similarity.TextSimilaritySearch;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class ImportDataTest {

    WikiDataSimpleService wikiDataSimpleService = new WikiDataSimpleService();

    public void importWikiJsons(File jsonsFolder, File libsFolder, String libName) throws Exception {

        TextSimilaritySearch lib = new TextSimilaritySearch(
                3, // criticalContentHitCount
                2, // criticalTitleHitCount
                0.5, // criticalScore
                1, // contentK
                3, // titleK
                2, // hitGramsCountLogA，此值越小，命中累计计数对结果的影响越大
                500, // gramsCountLogA，低长度文本有略微的领先优势，此值越小，低长度文本优势越明显
                10, // idfGrowthK, gram得分区分度，此值越大，得分梯度越大
                2, // n-gram中的n，n越大越严格
                libName);
        lib.textPreprocess.loadReplaceMapFromFile("./replace.txt");
        int maxCount = 40 * 10000;

        Queue<File> queue = new LinkedList<>();

        for (File file : jsonsFolder.listFiles()) {
            queue.add(file);
        }

        System.out.println("files count: " + queue.size());


        while (!queue.isEmpty()) {
            File file = queue.remove();
            if (file.getName().endsWith(".json") && !file.getName().startsWith(".")) {
                wikiDataSimpleService.dealJsonEntire(file, new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) {
                        lib.addText(strings.get(2), strings.get(1), strings.get(0), 1);
                    }
                });
            }
            if (lib.textsCount() > maxCount) break;
        }

        lib.update();

        File saveFile = new File(libsFolder, libName);

        TextSimilaritySearch.save(lib, saveFile, true);
    }

    @Test
    public void tryImportWikiJsons() throws Exception {

        importWikiJsons(new File("D:/user_dir/data/wiki_data/wiki_interesting_segment"),
                new File("D:/user_dir/data/knowledge_libs"), "wiki_interesting_lib");

    }

    @Test
    public void acTest() throws Exception {
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("D:/user_dir/data/knowledge_libs", "wiki_interesting_lib"), true);

        List<AC.MatchResult> mrs = null;
        int times = 1;
        for (int i = 0; i < times; i++) {
            mrs = lib.contentAC.indexOf("木卫二欧罗巴直径和质量第四大公转轨道距离木星第六一颗介绍木卫二");
        }
        System.out.println(mrs);
    }

    @Test
    public void tryWikiLibLoadSearch() throws Exception {
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("D:/user_dir/data/knowledge_libs", "wiki_interesting_lib"), true);

        // 参数重定义
        lib.changeArgs(3, 2, 0.5,
                1, 3, 2, 500, 10);

        List<SimilaritySearchResult> resultList = null;

//            resultList = lib.similaritySearch("木卫二(又名欧罗巴)是木星天然卫星中直径和质量第四大，公转轨道距离木星第六近的一颗。" +
//                    "介绍木卫二", 15);
        resultList = lib.similaritySearch("介绍木星的卫星木卫二", 15);
//            resultList = lib.similaritySearch("介绍岳飞写的《满江红》(怒发冲冠凭栏处)", 15);
//            resultList = lib.similaritySearch("介绍岳飞写的《满江红写怀》", 15);
//            resultList = lib.similaritySearch("孟浩然的诗 春晓 ", 15);
//            resultList = lib.similaritySearch("介绍苏轼写的念奴娇赤壁怀古", 15);
//            resultList = lib.similaritySearch("春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("孟浩然 春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("春晓 春眠不觉晓 处处闻啼鸟 ", 15);
//            resultList = lib.similaritySearch("金星的质量 ", 30);


        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }

        System.out.println("statistics: " + lib.getStatistics());
        // 未去重的Grams Count
        System.out.println("每个文章的平均grams个数: " + (lib.contentGramsCountSum + lib.titleGramsCountSum) / lib.textsCount());
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("内存占用: " + usedMemory / 1024.0 / 1024.0 / 1024.0 + "G");

    }

    @Test
    public void tryManager() throws Exception {
        TextLibManagement textLibManagement = new TextLibManagement(new File("C:/user_dir/data/knowledge_libs"));
        textLibManagement.loadLibFromDisk("wiki_interesting_lib");

        System.out.println(textLibManagement.similaritySearch("wiki_interesting_lib",
                "孟浩然的诗 春晓", 10));
    }
}
