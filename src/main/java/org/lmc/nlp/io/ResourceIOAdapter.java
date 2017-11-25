package org.lmc.nlp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ResourceIOAdapter
 */
public class ResourceIOAdapter implements IIOAdapter {
    /**
     * open a file from resources
     * @param path don't contain "/"
     * @return
     * @throws IOException
     */
    @Override
    public InputStream read(String path) throws IOException {
        return getClass().getResourceAsStream("/" + path);
    }

    /**
     * can not create a file of resources
     * @param path
     * @return
     * @throws IOException
     */
    @Override
    public OutputStream write(String path) throws IOException {
        return null;
    }
}
