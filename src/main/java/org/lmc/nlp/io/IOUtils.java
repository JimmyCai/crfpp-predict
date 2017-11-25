package org.lmc.nlp.io;

import org.lmc.nlp.util.StaticValue;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Common IO Operation
 *
 */
public class IOUtils {
    /**
     * Object Serialization
     * @param o object to save
     * @param path target path
     * @return if exception return false else return true
     */
    public static boolean saveObjectTo(Object o, String path) {
        try(ObjectOutputStream oos = new ObjectOutputStream(IOUtils.newOutputStream(path))) {
            oos.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Object Un-Serialization
     *
     * @param path source path
     * @return return target path
     */
    public static Object readObjectFrom(String path) {
        if(path == null || path.isEmpty()) return null;

        try(ObjectInputStream ois = new ObjectInputStream(IOUtils.newInputStream(path))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * read one file as string
     *
     * @param path source path
     * @return file content as one string
     */
    public static String readTxt(String path) {
        if (path == null) return null;

        try(InputStream is = StaticValue.ioAdapter.read(path)) {
            byte[] fileContent = new byte[is.available()];
            readBytesFromOtherInputStream(is, fileContent);
            return new String(fileContent, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read csv file
     * @param path source path
     * @return linkedList String Array
     */
    public static LinkedList<String[]> readCsv(String path) {
        LinkedList<String[]> resultList = new LinkedList<String[]>();
        LinkedList<String> lineList = readLineList(path);
        for (String line : lineList) {
            resultList.add(line.split(","));
        }
        return resultList;
    }

    /**
     * save content to file as stream
     *
     * @param path target path
     * @param content content to save
     * @return success or not
     */
    public static boolean saveTxt(String path, String content) {
        checkWriteCondition();

        if(path == null || path.isEmpty() || content == null || content.isEmpty()) return false;

        try(OutputStream fos = StaticValue.ioAdapter.write(path)) {
            ((FileOutputStream)fos).getChannel().write(ByteBuffer.wrap(content.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveTxt(String path, StringBuilder content)
    {
        return saveTxt(path, content.toString());
    }

    public static <T> boolean saveCollectionToTxt(Collection<T> collection, String path) {
        StringBuilder sb = new StringBuilder();
        for (Object o : collection) {
            sb.append(o);
            sb.append('\n');
        }
        return saveTxt(path, sb.toString());
    }

    /**
     * read file as byte[]
     *
     * @param path file path
     * @return byte[]
     */
    public static byte[] readBytes(String path) throws IOException {
        InputStream is = StaticValue.ioAdapter.read(path);

        byte[] bytes;
        if(StaticValue.ioAdapter instanceof FileIOAdapter) {
            bytes = readBytesFromFileInputStream((FileInputStream)is);
        } else {
            bytes = readBytesFromOtherInputStream(is);
        }

        return bytes;
    }

    /**
     * read file stream content as one string
     * @param file input file path
     * @param charsetName char-set
     * @return file content
     * @throws IOException exception
     */
    public static String readTxt(String file, String charsetName) throws IOException {
        byte[] bytes = readBytes(file);

        return new String(bytes, charsetName);
    }

    /**
     * read bytes from fileInputStream
     * Use FileChannel
     * @param fis file input stream
     * @return byte array
     * @throws IOException exception
     */
    private static byte[] readBytesFromFileInputStream(FileInputStream fis) throws IOException {
        FileChannel channel = fis.getChannel();
        int fileSize = (int) channel.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        channel.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        byteBuffer.clear();
        channel.close();
        fis.close();
        return bytes;
    }

    /**
     * read bytes from otherInputStream
     *
     * @param is inputStream
     * @return byte array
     * @throws IOException exception
     */
    public static byte[] readBytesFromOtherInputStream(InputStream is) throws IOException {
        byte[] targetArray = new byte[is.available()];
        readBytesFromOtherInputStream(is, targetArray);
        is.close();
        return targetArray;
    }

    /**
     * read bytes from otherInputStream with target array
     * @param is input stream
     * @param targetArray output
     * @throws IOException exception
     */
    public static void readBytesFromOtherInputStream(InputStream is, byte[] targetArray) throws IOException {
        int len;
        int off = 0;
        while ((len = is.read(targetArray, off, targetArray.length - off)) != -1 && off < targetArray.length) {
            off += len;
        }
    }

    public static LinkedList<String> readLineList(String path) {
        LinkedList<String> result = new LinkedList<String>();
        String txt = readTxt(path);
        if (txt == null) return result;
        StringTokenizer tokenizer = new StringTokenizer(txt, "\n");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result;
    }

    public static boolean saveMapToTxt(Map<Object, Object> map, String path)
    {
        return saveMapToTxt(map, path, "=");
    }

    public static boolean saveMapToTxt(Map<Object, Object> map, String path, String separator) {
        map = new TreeMap<Object, Object>(map);
        return saveEntrySetToTxt(map.entrySet(), path, separator);
    }

    public static boolean saveEntrySetToTxt(Set<Map.Entry<Object, Object>> entrySet, String path, String separator) {
        StringBuilder sbOut = new StringBuilder();
        for (Map.Entry<Object, Object> entry : entrySet) {
            sbOut.append(entry.getKey());
            sbOut.append(separator);
            sbOut.append(entry.getValue());
            sbOut.append('\n');
        }
        return saveTxt(path, sbOut.toString());
    }

    public static LineIterator readLine(String path)
    {
        return new LineIterator(path);
    }

    /**
     * 创建一个BufferedWriter
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static BufferedWriter newBufferedWriter(String path) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(IOUtils.newOutputStream(path), "UTF-8"));
    }

    /**
     * 创建一个BufferedReader
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static BufferedReader newBufferedReader(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(IOUtils.newInputStream(path), "UTF-8"));
    }

    public static BufferedWriter newBufferedWriter(String path, boolean append) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), "UTF-8"));
    }

    /**
     * create input stream
     * @param path target path
     * @return input stream
     * @throws IOException exception
     */
    public static InputStream newInputStream(String path) throws IOException {
        return StaticValue.ioAdapter.read(path);
    }

    /**
     * create output stream
     * @param path target path
     * @return output stream
     * @throws IOException exception
     */
    public static OutputStream newOutputStream(String path) throws IOException {
        checkWriteCondition();
        return StaticValue.ioAdapter.write(path);
    }

    private static boolean checkWriteCondition() {
        return StaticValue.ioAdapter instanceof FileIOAdapter;
    }
}
