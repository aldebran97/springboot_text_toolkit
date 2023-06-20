package com.aldebran.text;

import cn.hutool.core.io.FileUtil;
import com.aldebran.text.similarity.SimilaritySearchResult;
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
            String text = title + " " + segment.toString();
            String thisId = id + "_" + count;
            lib.addText(text, title, thisId);
            count++;
//            System.out.printf("add %s %s %s %n", thisId, title, text);
        }
        return 1;
    }


    public void importWikiJsons(File jsonsFolder, File libsFolder, String libName) throws Exception {
        TextSimilaritySearch lib = new TextSimilaritySearch(
                libName, 1, 0.5, 2, 0.3);
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
        importWikiJsons(new File("/Users/aldebran/custom/data/wiki/wiki_jsons1_segment"),
                new File("/Users/aldebran/custom/data/wiki/libs"), "wiki_jsons1_lib");

    }

    @Test
    public void tryLoadSearch() throws Exception {
        long loadSt = System.currentTimeMillis();
        TextSimilaritySearch lib = TextSimilaritySearch.load(
                new File("/Users/aldebran/custom/data/wiki/libs", "wiki_jsons1_lib"));
        long loadEd = System.currentTimeMillis();
        System.out.println("load time: " + (loadEd - loadSt) / 1000.0 + "s");

        lib.decayRate = 0.01;
        long searchSt = System.currentTimeMillis();
        List<SimilaritySearchResult> resultList = lib.similaritySearch("木卫二(欧罗巴)是直径和质量第四大，公转轨道距离木星第六近的一颗。介绍木卫二", 10);
        long searchEd = System.currentTimeMillis();
        for (SimilaritySearchResult similaritySearchResult : resultList) {
            System.out.println(similaritySearchResult);
        }
        System.out.println("search time: " + (searchEd - searchSt) / 1000.0 + "s");
        System.out.println("texts count: " + lib.textsCount());

    }
}
