package com.ccx.spark.demo;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5 32位密文
 * @classname MD5
 * @author lilin
 * @date 2016年11月10日 上午11:36:15
 * @version
 */
public class MD5 {
	
    /**
    *
    * @param plainText
    *            明文
    * @return 32位密文
    */
   public static String encryptionUtf8(String plainText) {
       String re_md5 = new String();
       try {
           MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(plainText.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
           byte b[] = md.digest();
           int i;
           StringBuffer buf = new StringBuffer("");
           for (int offset = 0; offset < b.length; offset++) {
               i = b[offset];
               if (i < 0)
                   i += 256;
               if (i < 16)
                   buf.append("0");
               buf.append(Integer.toHexString(i));
           }
           re_md5 = buf.toString();
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
       return re_md5;
   }
   
   public static String encryptionGbk(String plainText) {
       String re_md5 = new String();
       try {
           MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(plainText.getBytes("GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
           byte b[] = md.digest();
           int i;
           StringBuffer buf = new StringBuffer("");
           for (int offset = 0; offset < b.length; offset++) {
               i = b[offset];
               if (i < 0)
                   i += 256;
               if (i < 16)
                   buf.append("0");
               buf.append(Integer.toHexString(i));
           }
           re_md5 = buf.toString();
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
       return re_md5;
   }
   
   public static void main(String[] args)
   {
   	 String sign = encryptionUtf8("王植");
   	 System.out.println(sign);
   	 
   	String signGbk = encryptionGbk("何鹏").toUpperCase();
  	 System.out.println(signGbk);
   	 
   }
}
