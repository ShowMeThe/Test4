package com.example.ken.test4;

import android.graphics.PointF;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ken on 2017/12/28.
 */

public class MathUtil {

    public static String getMd5_32(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuilder bud= new StringBuilder();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    bud.append("0");
                bud.append(Integer.toHexString(i));
            }
            return bud.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static  String getMd5_16(String sSecret){
        return getMd5_32(sSecret).substring(8,24);
    }


    public static float getPointDis(PointF start,PointF end){
          float xOffset = Math.abs(start.x - end.x);
          float yOffset = Math.abs(start.y - end.y);
        return (float) Math.sqrt(xOffset*xOffset + yOffset*yOffset);
    }


}
