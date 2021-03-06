package com.nala.faceCatch.util;

/**
 * create by lizenn
 * create date 2018/7/25
 * description
 */
public class NumberUtil {

    public static void convertBinary(int num) {
        int binary[] = new int[50];
        int index = 0;
        if (num > 0) {
            while (num > 0) {
                binary[index++] = num % 2;
                num = num / 2;
            }
            for (int i = index - 1; i >= 0; i--) {
                System.out.print(binary[i]);
//                System.out.println(" ");
            }
        } else if (num < 0) {
            int tempNum = -num;
            while (tempNum > 0) {
                binary[index++] = tempNum % 2;
                tempNum = tempNum / 2;
            }

            //取反码
            for (int i = 0; i < index; i++) {
                if (binary[i] == 0)
                    binary[i] = 1;
                if (binary[i] == 1)
                    binary[i] = 0;
            }
            //取补码
            for (int i = index - 1; i >= 0; i--) {
                if (binary[i] == 0) {
                    binary[i] = 1;
                } else if (binary[i] == 1) {
                    binary[i] = 0;
                    if (binary[i - 1] == 0) {
                        binary[i - 1] = 1;
                    }
                }
                System.out.print(binary[i]);
            }
        }


    }

    /**
     * 十进制转二进制，获取末尾8位二进制码
     *
     * @param num
     */
    public static String binaryString(int num) {
        String bitString = null;

        bitString = Integer.toBinaryString(num);
        int flag = 8 - bitString.length();
        if (flag > 0) {
            for (int i = 1; i <= flag; i++) {
                bitString = "0" + bitString;
            }

        } else if (flag < 0) {
            bitString = bitString.substring(bitString.length() - 8, bitString.length());
        }
        System.out.println("bit===>" + bitString);
        return bitString;
    }
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
}
