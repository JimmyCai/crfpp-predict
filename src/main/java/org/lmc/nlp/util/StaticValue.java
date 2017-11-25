package org.lmc.nlp.util;

import org.lmc.nlp.io.FileIOAdapter;
import org.lmc.nlp.io.IIOAdapter;
import org.lmc.nlp.io.ResourceIOAdapter;

/**
 * Some Static Value
 */
public class StaticValue {
    private static final IIOAdapter resourceIOAdapter = new ResourceIOAdapter();
    private static final IIOAdapter fileIOAdpater = new FileIOAdapter();

    public static IIOAdapter ioAdapter = resourceIOAdapter;

    public final static String BIN_EXT = ".bin";

    public static void setResourceIOAdapter() {
        ioAdapter = resourceIOAdapter;
    }

    public static void setFileIOAdpater() {
        ioAdapter = fileIOAdpater;
    }
}
