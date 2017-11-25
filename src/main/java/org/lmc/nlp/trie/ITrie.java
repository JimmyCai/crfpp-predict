package org.lmc.nlp.trie;

import org.lmc.nlp.io.ByteArray;

import java.io.DataOutputStream;
import java.util.TreeMap;

/**
 * ITrie
 * Created by limingcai on 2017/11/25.
 */
public interface ITrie<V>
{
    int build(TreeMap<String, V> keyValueMap);
    boolean save(DataOutputStream out);
    boolean load(ByteArray byteArray, V[] value);
    V get(char[] key);
    V get(String key);
    V[] getValueArray(V[] a);
    boolean containsKey(String key);
    int size();
}

