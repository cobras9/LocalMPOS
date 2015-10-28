package com.mobilis.android.nfc.util;

/**
 * Created by lewischao on 12/02/15.
 */
public class ConversionUtil {
    /**
     * @param src bytes read from tags
     * @return converts UID byte array to String of Hex
     */
    public static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString().substring(2);
    }
    public static String bytesToHexString(byte src) {

        StringBuilder stringBuilder = new StringBuilder("0x");

        char[] buffer = new char[2];
            buffer[0] = Character.forDigit((src >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src & 0x0F, 16);
            stringBuilder.append(buffer);
        return stringBuilder.toString().substring(2);
    }

    /**
     * Converts the HEX string to byte array.
     *
     * @param hexString the HEX string.
     * @return the number of bytes.
     */
    public static int toByteArray(String hexString, byte[] byteArray) {

        char c = 0;
        boolean first = true;
        int length = 0;
        int value = 0;
        int i = 0;

        for (i = 0; i < hexString.length(); i++) {

            c = hexString.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                value = c - '0';
            } else if ((c >= 'A') && (c <= 'F')) {
                value = c - 'A' + 10;
            } else if ((c >= 'a') && (c <= 'f')) {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[length] = (byte) (value << 4);

                } else {

                    byteArray[length] |= value;
                    length++;
                }

                first = !first;
            }

            if (length >= byteArray.length) {
                break;
            }
        }

        return length;
    }
}
