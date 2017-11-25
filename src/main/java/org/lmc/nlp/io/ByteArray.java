package org.lmc.nlp.io;

import org.lmc.nlp.util.ByteUtil;

/**
 * ByteArray
 * Created by limingcai on 2017/11/25.
 */
public class ByteArray {
    private byte[] bytes;
    private int offset;

    public ByteArray(byte[] bytes) {
        this.bytes = bytes;
    }

    public static ByteArray createByteArray(String path) {
        try {
            byte[] bytes = IOUtils.readBytes(path);
            if (bytes == null) return null;
            return new ByteArray(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int nextInt() {
        int result = ByteUtil.bytesHighFirstToInt(bytes, offset);
        offset += 4;
        return result;
    }

    public double nextDouble() {
        double result = ByteUtil.bytesHighFirstToDouble(bytes, offset);
        offset += 8;
        return result;
    }

    public String nextString() {
        char[] buffer = new char[nextInt()];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = nextChar();
        }
        return new String(buffer);
    }

    public float nextFloat() {
        float result = ByteUtil.bytesHighFirstToFloat(bytes, offset);
        offset += 4;
        return result;
    }

    public char nextChar() {
        char result = ByteUtil.bytesHighFirstToChar(bytes, offset);
        offset += 2;
        return result;
    }

    public byte nextByte() {
        return bytes[offset++];
    }

    public int nextUnsignedShort() {
        byte a = nextByte();
        byte b = nextByte();
        return (((a & 0xff) << 8) | (b & 0xff));
    }

    public String nextUTF() {
        int utflen = nextUnsignedShort();
        byte[] bytearr = null;
        char[] chararr = null;
        bytearr = new byte[utflen];
        chararr = new char[utflen];

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        for (int i = 0; i < utflen; ++i) {
            bytearr[i] = nextByte();
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        System.out.println("malformed input: partial character at end");
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        System.out.println("malformed input around byte " + count);
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        System.out.println("malformed input: partial character at end");
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        System.out.println("malformed input around byte " + (count - 1));
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6) |
                            ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    System.out.println("malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }

    public boolean hasMore() {
        return offset < bytes.length;
    }

    public void close() {
        bytes = null;
    }

    public int getLength() {
        return bytes.length;
    }
}
