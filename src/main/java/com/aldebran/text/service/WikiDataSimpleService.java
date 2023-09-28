package com.aldebran.text.service;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * wiki数据服务-简易
 *
 * @author aldebran
 * @since 2023-09-28
 */
@Service
public class WikiDataSimpleService {

    public void dealJsonSegment(File inFile, Consumer<List<String>> consumer) throws Exception {
        String fileContent = FileUtil.readString(inFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(fileContent);
        String title = jsonObject.getString("title");
        if (title.endsWith("列表")) {
            return;
        }
        for (Object segment : jsonObject.getJSONArray("segments")) {
            String segmentS = segment.toString();
            String text = segmentS;
            String thisId = UUID.randomUUID().toString();
            consumer.accept(Arrays.asList(thisId, title, text));

//            System.out.printf("add %s %s %s %n", thisId, title, text);
        }
    }


    public void dealJsonEntire(File inFile, Consumer<List<String>> consumer) throws Exception {
        String fileContent = FileUtil.readString(inFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(fileContent);
        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        if (title.endsWith("列表")) {
            return;
        }
        String thisId = UUID.randomUUID().toString();
        System.out.printf("add %s %s %n", id, title);
        StringBuilder sb = new StringBuilder();
        for (Object segment : jsonObject.getJSONArray("segments")) {
            String segmentS = segment.toString();
            sb.append(segmentS);
            sb.append("\n");
        }
        consumer.accept(Arrays.asList(thisId, title, sb.toString()));
//        System.out.printf("add %s %s %s %n", thisId, title, text);
    }

    public void dealJsonSplit(File inFile, Consumer<List<String>> consumer, int length) throws Exception {
        String fileContent = FileUtil.readString(inFile, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(fileContent);
        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        if (title.endsWith("列表")) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Object segment : jsonObject.getJSONArray("segments")) {
            String segmentS = segment.toString();
            sb.append(segmentS);
            sb.append("\n");
        }
        int st = 0;
        while (sb.length() - st >= length) {
            String thisId = UUID.randomUUID().toString();
            consumer.accept(Arrays.asList(thisId, title, sb.substring(st, st + length)));
//            System.out.printf("add %s %s %s%n", thisId, title, sb.substring(st, st + length));
            st += length;
        }
    }

}
