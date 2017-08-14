package com.tim.app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @创建者 倪军
 * @创建时间 2017/8/14
 * @描述
 */

public class EncryptUtil {

    public static String md5(String str){

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuilder result = new StringBuilder();
            for (Byte b : bytes){
                String temp = Integer.toHexString(b & 0xff);
                if(temp.length() == 1){
                    temp = "0" +temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
