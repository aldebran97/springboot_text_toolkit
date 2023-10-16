package com.aldebran.web_text;

import com.aldebran.text.similarity.BasicText;
import com.aldebran.text.similarity.TextPreprocess;
import org.junit.jupiter.api.Test;

/**
 * 文本预处理工具测试
 *
 * @author aldebran
 * @since 2023-09-23
 */
public class TextPreprocessTest {

    String text = "SUV的全称是Sport Utility Vehicle，中文意思是运动型多用途汽车。主要是指那些设计前卫、造型新颖的四轮驱动越野车。" +
            "假设这辆SUV价格是40.56万元。";

    @Test
    public void preprocess() throws Exception {
        TextPreprocess textPreprocess = new TextPreprocess();
        textPreprocess.loadReplaceMapFromFile("./replace.txt");
        System.out.println(textPreprocess.preprocess(text));

        textPreprocess = new TextPreprocess();
        System.out.println(textPreprocess.preprocess(text));
    }

    @Test
    public void nGram() throws Exception {
        TextPreprocess textPreprocess = new TextPreprocess();
        textPreprocess.loadReplaceMapFromFile("./replace.txt");
        BasicText basicText = textPreprocess.textProcess(text);
        System.out.println(textPreprocess.nGram(basicText, 3));
    }
}
