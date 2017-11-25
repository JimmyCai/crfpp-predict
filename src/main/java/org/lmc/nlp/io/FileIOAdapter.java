package org.lmc.nlp.io;

import java.io.*;

/**
 * FileIOAdapter
 */
public class FileIOAdapter implements IIOAdapter {
    /**
     * open a file from path
     * @param path
     * @return
     * @throws IOException
     */
    @Override
    public FileInputStream read(String path) throws IOException {
        return new FileInputStream(path);
    }

    /**
     * create a file from path
     * @param path
     * @return
     * @throws IOException
     */
    @Override
    public FileOutputStream write(String path) throws IOException {
        return new FileOutputStream(path);
    }
}
