package com.qianyishen.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于sha加密的类
 * @author user
 */
@Component("hashUtil")
public class HashUtil {
    /**
     * 传入文本内容，返回 SHA-256 串
     * @param strText 加密内容
     * @return 加密后的字符串
     */
    public static String sha256(final String strText) {
        return sha(strText, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     * @param strText 要加密的字符串
     * @return 加密后的字符串
     */
    public static String sha512(final String strText) {
        return sha(strText, "SHA-512");
    }

    /**
     * md5加密
     * @param strText 要加密的字符串
     * @return 加密后的字符串
     */
    public static String shaMd5(String strText) {
        return sha(strText, "MD5");
    }

    /**
     * 字符串 SHA 加密
     * @param strText 要加密的字符串
     * @param strType 加密的类型
     * @return 加密后的字符串
     */
    private static String sha(final String strText, final String strType) {
        // 返回值
        String strResult = null;

        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                byte[] byteBuffer = messageDigest.digest();

                // 將 byte 轉換爲 string
                StringBuffer strHexString = new StringBuffer();
                // 遍歷 byte buffer
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }

}
