package org.lmc.nlp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IIOAdapter
 */
public interface IIOAdapter
{
    /**
     * open a file as input stream
     * @param path
     * @return
     * @throws IOException
     */
    InputStream read(String path) throws IOException;

    /**
     * create a file as output stream
     * @param path
     * @return
     * @throws IOException
     */
    OutputStream write(String path) throws IOException;
}
