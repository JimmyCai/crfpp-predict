package org.lmc.nlp.crfpp.predict;

import org.lmc.nlp.io.ByteArray;
import org.lmc.nlp.io.ICacheAble;
import org.lmc.nlp.io.IOUtils;
import org.lmc.nlp.io.LineIterator;
import org.lmc.nlp.trie.DoubleArrayTrie;
import org.lmc.nlp.trie.ITrie;
import org.lmc.nlp.util.StaticValue;

import java.io.DataOutputStream;
import java.util.*;

/**
 * CRFModel
 */
public class CRFModel implements ICacheAble {
    /**
     * tag to id
     */
    private Map<String, Integer> tag2id;
    /**
     * id to tag
     */
    private String[] id2tag;
    /**
     * feature function
     */
    private ITrie<FeatureFunction> featureFunctionTrie;
    /**
     * feature template
     */
    private List<FeatureTemplate> featureTemplateList;
    /**
     * bigram
     */
    private double[][] matrix;

    public CRFModel() {
        featureFunctionTrie = new DoubleArrayTrie<FeatureFunction>();
    }

    public CRFModel(ITrie<FeatureFunction> featureFunctionTrie) {
        this.featureFunctionTrie = featureFunctionTrie;
    }

    public static CRFModel loadTxt(String path, CRFModel instance) {
        CRFModel CRFModel = instance;
        // load from bin first
        if (CRFModel.load(ByteArray.createByteArray(path + StaticValue.BIN_EXT))) return CRFModel;

        LineIterator lineIterator = new LineIterator(path);
        if (!lineIterator.hasNext()) return null;
        lineIterator.next();   // verson
        lineIterator.next();   // cost-factor
        lineIterator.next();   //max-id
        lineIterator.next();   // xsize
        lineIterator.next();    // blank

        String line;
        int id = 0;
        CRFModel.tag2id = new HashMap<>();
        while ((line = lineIterator.next()).length() != 0) {
            CRFModel.tag2id.put(line, id);
            ++id;
        }

        CRFModel.id2tag = new String[CRFModel.tag2id.size()];
        final int size = CRFModel.id2tag.length;
        for (Map.Entry<String, Integer> entry : CRFModel.tag2id.entrySet()) {
            CRFModel.id2tag[entry.getValue()] = entry.getKey();
        }

        TreeMap<String, FeatureFunction> featureFunctionMap = new TreeMap<>();
        List<FeatureFunction> featureFunctionList = new LinkedList<>();
        CRFModel.featureTemplateList = new LinkedList<>();

        while ((line = lineIterator.next()).length() != 0) {
            if (!"B".equals(line)) {
                FeatureTemplate featureTemplate = FeatureTemplate.create(line);
                CRFModel.featureTemplateList.add(featureTemplate);
            }
            else {
                CRFModel.matrix = new double[size][size];
            }
        }

        if (CRFModel.matrix != null) {
            lineIterator.next();    // 0 B
        }

        while ((line = lineIterator.next()).length() != 0) {
            String[] args = line.split(" ", 2);
            char[] charArray = args[1].toCharArray();
            FeatureFunction featureFunction = new FeatureFunction(charArray, size);
            featureFunctionMap.put(args[1], featureFunction);
            featureFunctionList.add(featureFunction);
        }

        if (CRFModel.matrix != null) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    CRFModel.matrix[i][j] = Double.parseDouble(lineIterator.next());
                }
            }
        }

        for (FeatureFunction featureFunction : featureFunctionList) {
            for (int i = 0; i < size; i++) {
                featureFunction.weights[i] = Double.parseDouble(lineIterator.next());
            }
        }
        if (lineIterator.hasNext()) {
            System.out.println("May Be Error！" + path);
        }
        lineIterator.close();

        CRFModel.featureFunctionTrie.build(featureFunctionMap);

        try {
            DataOutputStream out = new DataOutputStream(IOUtils.newOutputStream(path + StaticValue.BIN_EXT));
            CRFModel.save(out);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return CRFModel;
    }

    public void tag(Table table, boolean saveScore) {
        int size = table.size();
        if (size == 0) return;
        int tagSize = id2tag.length;
        double[][] net = new double[size][tagSize];

        for (int i = 0; i < size; ++i) {
            LinkedList<double[]> scoreList = computeScoreList(table, i);
            for (int tag = 0; tag < tagSize; ++tag) {
                net[i][tag] = computeScore(scoreList, tag);
            }
        }

        if(saveScore) {
            calScore(table, net);
        }

        if (size == 1) {
            double maxScore = -1e10;
            int bestTag = 0;
            for (int tag = 0; tag < net[0].length; ++tag) {
                if (net[0][tag] > maxScore) {
                    maxScore = net[0][tag];
                    bestTag = tag;
                }
            }
            table.setLast(0, id2tag[bestTag]);
            return;
        }

        int[][] from = new int[size][tagSize];
        for (int i = 1; i < size; ++i) {
            for (int now = 0; now < tagSize; ++now) {
                double maxScore = -1e10;
                for (int pre = 0; pre < tagSize; ++pre) {
                    double score = net[i - 1][pre] + matrix[pre][now] + net[i][now];
                    if (score > maxScore) {
                        maxScore = score;
                        from[i][now] = pre;
                    }
                }
                net[i][now] = maxScore;
            }
        }
        // 反向回溯最佳路径
        double maxScore = -1e10;
        int maxTag = 0;
        for (int tag = 0; tag < net[size - 1].length; ++tag) {
            if (net[size - 1][tag] > maxScore) {
                maxScore = net[size - 1][tag];
                maxTag = tag;
            }
        }

        table.setLast(size - 1, id2tag[maxTag]);
        maxTag = from[size - 1][maxTag];
        for (int i = size - 2; i > 0; --i) {
            table.setLast(i, id2tag[maxTag]);
            maxTag = from[i][maxTag];
        }
        table.setLast(0, id2tag[maxTag]);
    }

    public void greedyTag(Table table, boolean saveScore) {
        int size = table.size();
        if (size == 0) return;
        int tagSize = id2tag.length;
        double[][] net = new double[size][tagSize];

        for (int i = 0; i < size; ++i) {
            LinkedList<double[]> scoreList = computeScoreList(table, i);
            for (int tag = 0; tag < tagSize; ++tag) {
                net[i][tag] = computeScore(scoreList, tag);
            }
        }

        if(saveScore) {
            calScore(table, net);
        }

        double maxScore = -1e10;
        int maxTag = 0;
        for (int tag = 0; tag < net[0].length; ++tag) {
            if (net[0][tag] > maxScore) {
                maxScore = net[0][tag];
                maxTag = tag;
            }
        }
        table.setLast(0, id2tag[maxTag]);
        double curScore = maxScore;
        int preTag = maxTag;

        for (int i = 1; i < size; ++i) {
            maxScore = -1e10;
            for (int j = 0; j < net[i].length; ++ j) {
                double score = curScore + matrix[preTag][j] + net[i][j];
                if(score > maxScore) {
                    maxScore = score;
                    maxTag = j;
                }
            }
            table.setLast(i, id2tag[maxTag]);
            preTag = maxTag;
            curScore = maxScore;
        }
    }

    private void calScore(Table table, double[][] net) {
        int size = table.size();
        int tagSize = id2tag.length;
        table.score = new double[size][tagSize];
        for (int i = 0; i < size; ++i) {
            double curSum = 0;
            for (int j = 0; j < tagSize; ++j) {
                curSum += Math.pow(Math.E, net[i][j]);
            }
            for ( int j = 0; j < tagSize; ++j) {
                table.score[i][j] = Math.pow(Math.E, net[i][j]) / curSum;
            }
        }
    }

    private LinkedList<double[]> computeScoreList(Table table, int current) {
        LinkedList<double[]> scoreList = new LinkedList<>();
        for (FeatureTemplate featureTemplate : featureTemplateList) {
            char[] o = featureTemplate.generateParameter(table, current);
            FeatureFunction featureFunction = featureFunctionTrie.get(o);
            if (featureFunction == null) continue;
            scoreList.add(featureFunction.weights);
        }

        return scoreList;
    }

    private static double computeScore(LinkedList<double[]> scoreList, int tag) {
        double score = 0;
        for (double[] w : scoreList) {
            score += w[tag];
        }
        return score;
    }

    @Override
    public void save(DataOutputStream out) throws Exception {
        out.writeInt(id2tag.length);
        for (String tag : id2tag) {
            out.writeUTF(tag);
        }
        FeatureFunction[] valueArray = featureFunctionTrie.getValueArray(new FeatureFunction[0]);
        out.writeInt(valueArray.length);
        for (FeatureFunction featureFunction : valueArray) {
            featureFunction.save(out);
        }
        featureFunctionTrie.save(out);
        out.writeInt(featureTemplateList.size());
        for (FeatureTemplate featureTemplate : featureTemplateList) {
            featureTemplate.save(out);
        }
        if (matrix != null) {
            out.writeInt(matrix.length);
            for (double[] line : matrix) {
                for (double v : line) {
                    out.writeDouble(v);
                }
            }
        }
        else {
            out.writeInt(0);
        }
    }

    @Override
    public boolean load(ByteArray byteArray) {
        if (byteArray == null) return false;
        try {
            int size = byteArray.nextInt();
            id2tag = new String[size];
            tag2id = new HashMap<>(size);
            for (int i = 0; i < id2tag.length; i++) {
                id2tag[i] = byteArray.nextUTF();
                tag2id.put(id2tag[i], i);
            }
            FeatureFunction[] valueArray = new FeatureFunction[byteArray.nextInt()];
            for (int i = 0; i < valueArray.length; i++) {
                valueArray[i] = new FeatureFunction();
                valueArray[i].load(byteArray);
            }
            featureFunctionTrie.load(byteArray, valueArray);
            size = byteArray.nextInt();
            featureTemplateList = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                FeatureTemplate featureTemplate = new FeatureTemplate();
                featureTemplate.load(byteArray);
                featureTemplateList.add(featureTemplate);
            }
            size = byteArray.nextInt();
            if (size == 0) return true;
            matrix = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = byteArray.nextDouble();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static CRFModel loadTxt(String path) {
        return loadTxt(path, new CRFModel(new DoubleArrayTrie<FeatureFunction>()));
    }

    public static CRFModel load(String path) {
        CRFModel model = loadBin(path + StaticValue.BIN_EXT);
        if (model != null) return model;
        return loadTxt(path, new CRFModel(new DoubleArrayTrie<FeatureFunction>()));
    }

    public static CRFModel loadBin(String path) {
        ByteArray byteArray = ByteArray.createByteArray(path);
        if (byteArray == null) return null;
        CRFModel model = new CRFModel();
        if (model.load(byteArray)) return model;
        return null;
    }

    public Integer getTagId(String tag) {
        return tag2id.get(tag);
    }

    public String getIdTag(int id) {
        return id2tag[id];
    }
}
