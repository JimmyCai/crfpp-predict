package org.lmc.nlp.crfpp.predict;

import org.lmc.nlp.io.ByteArray;
import org.lmc.nlp.io.ICacheAble;

import java.io.DataOutputStream;

/**
 * Feature Function
 */
public class FeatureFunction implements ICacheAble {
    char[] featureStr;

    /**
     * weights
     */
    double[] weights;

    public FeatureFunction(char[] featureStr, int tagSize)
    {
        this.featureStr = featureStr;
        weights = new double[tagSize];
    }

    public FeatureFunction()
    {
    }

    @Override
    public void save(DataOutputStream out) throws Exception
    {
        out.writeInt(featureStr.length);
        for (char c : featureStr)
        {
            out.writeChar(c);
        }
        out.writeInt(weights.length);
        for (double v : weights)
        {
            out.writeDouble(v);
        }
    }

    @Override
    public boolean load(ByteArray byteArray)
    {
        int size = byteArray.nextInt();
        featureStr = new char[size];
        for (int i = 0; i < size; ++i)
        {
            featureStr[i] = byteArray.nextChar();
        }
        size = byteArray.nextInt();
        weights = new double[size];
        for (int i = 0; i < size; ++i)
        {
            weights[i] = byteArray.nextDouble();
        }
        return true;
    }
}
