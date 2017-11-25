package org.lmc.nlp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Line Iterator
 */
public class LineIterator {
    private BufferedReader bw;
    private String line;

    public LineIterator(String path) {
        try {
            bw = new BufferedReader(new InputStreamReader(IOUtils.newInputStream(path), "UTF-8"));
            line = bw.readLine();
        }
        catch (Exception e) {
            e.printStackTrace();
            bw = null;
        }
    }

    public void close() {
        if (bw == null) return;
        try {
            bw.close();
            bw = null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public boolean hasNext() {
        if (bw == null) return false;
        if (line == null) {
            try {
                bw.close();
                bw = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;
    }

    public String next() {
        String preLine = line;
        try {
            if (bw != null) {
                line = bw.readLine();
                if (line == null && bw != null) {
                    try {
                        bw.close();
                        bw = null;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                line = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return preLine;
    }
}
