package org.lmc.nlp.crfpp.predict;

/**
 * Table
 */
public class Table {
    public String[][] inputs;
    private static final String HEAD = "_B";
    public double[][] score;

    @Override
    public String toString()
    {
        if (inputs == null) return "null";
        final StringBuilder sb = new StringBuilder(inputs.length * inputs[0].length * 2);
        for (String[] line : inputs)
        {
            for (String element : line)
            {
                sb.append(element).append('\t');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String get(int x, int y)
    {
        if (x < 0) return HEAD + x;
        if (x >= inputs.length) return HEAD + "+" + (x - inputs.length + 1);

        return inputs[x][y];
    }

    public void setLast(int x, String t)
    {
        inputs[x][inputs[x].length - 1] = t;
    }

    public int size()
    {
        return inputs.length;
    }
}
