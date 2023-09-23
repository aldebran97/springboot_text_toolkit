package com.aldebran.text.similarity;

import java.io.Serializable;

/**
 * 平均IDF增加值计算器
 *
 * @author aldebran
 * @since 2023-09-23
 */
public class AvgIdfGrowthCalculator implements Serializable {

    public double basicGrowthValue;

    public double gramAvgIdf;

    public double gramMinIdf;

    public double gramMaxIdf;

    public double titleIdfRate;


    // 可以是均匀线性增长、tan、sigmoid...等。对称中心在（gramAvgIdf , basicGrowthValue）
    public double calc(double gramIdf, boolean isTitle) {
        if (isTitle) {
            gramIdf *= titleIdfRate;
        }
        return gramIdf / gramAvgIdf * basicGrowthValue;
    }

    public void update(double basicGrowthValue, double gramAvgIdf, double gramMinIdf, double gramMaxIdf, double titleIdfRate) {
        this.basicGrowthValue = basicGrowthValue;
        this.gramAvgIdf = gramAvgIdf;
        this.gramMinIdf = gramMinIdf;
        this.gramMaxIdf = gramMaxIdf;
        this.titleIdfRate = titleIdfRate;
    }
}
