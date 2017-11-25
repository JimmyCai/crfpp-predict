package org.lmc.nlp.crfpp.predict;

import org.lmc.nlp.util.StaticValue;

public class CRFModelFacade {

    public static CRFModel getFromResources(String path) {
        StaticValue.setResourceIOAdapter();

        return CRFModel.load(path);
    }

    public static CRFModel getFromPath(String path) {
        StaticValue.setFileIOAdpater();

        return CRFModel.load(path);
    }

    public static Result predict(CRFModel model, String[][] input, boolean calScore) {
        Table table = new Table();
        table.inputs = input;

        model.tag(table, calScore);

        String[] tags = new String[input.length];
        int size = input[0].length;
        for(int i = 0; i < input.length; i++) {
            tags[i] = input[i][size - 1];
        }

        return new Result(tags, table.score);
    }

    public static Result predict(CRFModel model, String[][] input) {
        return predict(model, input, false);
    }

    public static Result greedyPredict(CRFModel model, String[][] input, boolean calScore) {
        Table table = new Table();
        table.inputs = input;

        model.greedyTag(table, calScore);

        String[] tags = new String[input.length];
        int size = input[0].length;
        for(int i = 0; i < input.length; i++) {
            tags[i] = input[i][size - 1];
        }

        return new Result(tags, table.score);
    }

    public static Result greedyPredict(CRFModel model, String[][] input) {
        return greedyPredict(model, input, false);
    }
}
