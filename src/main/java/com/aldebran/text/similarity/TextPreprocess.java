package com.aldebran.text.similarity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 文本预处理
 *
 * @author aldebran
 * @since 2023-09-23
 */
public class TextPreprocess implements Serializable {

    // 替换原则
    // （1）停止词 减少干扰
    // （2）相似词，增加召回率
    // （3）简称换全称，带来更多信息，增加召回率
    // （4）额外追加父类信息，父类level不能太高，否则精确率会降低
    public Map<String, String> replaceMap = new HashMap<>();



    public static Pattern whiteCharsPattern = Pattern.compile("\\s+");


    public String processText(String origin) {
        return null;
    }

    public void loadReplaceMapFromFile(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) continue;
                if (line.isEmpty()) continue;
                List<String> list = new ArrayList<>();
                for (String s : whiteCharsPattern.split(line)) {
                    if (!s.isEmpty()) {
                        list.add(line);
                    }
                }
                replaceMap.put(list.get(0), list.get(1) == null ? "" : list.get(1));
            }
        }
    }


}
