package com.fenix.spirometer.ble;

import java.math.BigDecimal;

public class DataUtils {
    private static final String HEX_STRING = "0123456789ABCDEF";
    private final static byte[] hex = HEX_STRING.getBytes();

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    public static byte[] subByteArray(byte[] src, int start, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, start, result, 0, length);
        return result;

    }


    public static byte charToByte(char c) {
        return (byte) HEX_STRING.indexOf(c);
    }

    public static int voltageToFlow(int voltage) {
        return (int) (0.0275f * voltage - 825);
    }
}
