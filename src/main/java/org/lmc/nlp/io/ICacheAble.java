package org.lmc.nlp.io;

import java.io.DataOutputStream;

/**
 * ICacheAble
 */
public interface ICacheAble {

    void save(DataOutputStream out) throws Exception;

    boolean load(ByteArray byteArray);
}
