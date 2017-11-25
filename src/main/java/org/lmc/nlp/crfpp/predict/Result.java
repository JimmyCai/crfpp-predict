package org.lmc.nlp.crfpp.predict;

/**
 * Predict Result
 */
public class Result {
    private String[] tags;
    private double[][] score;

    public Result(String[] tags, double[][] score)
    {
        this.tags = tags;
        this.score = score;
    }

    public String[] getTags() {
        return tags;
    }

    public double[][] getScore() {
        return score;
    }
}
