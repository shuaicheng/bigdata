package com.ccx.spark.demo;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util
{
	private static Logger log = LoggerFactory.getLogger(MD5Util.class);
    public final static String MD5(String s)
    {
        //char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        // update MD5 string to lower-case to adapt to UMP test
        // TODO: check lower-case or upperp-case are the MD5 standard
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try
        {
            byte[] btInput = s.getBytes();
            
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            
            mdInst.update(btInput);
            
            byte[] md = mdInst.digest();
            
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e)
        {
        	log.error("MD5 Exception",e);
            return null;
        }
    }

    public static final String Md(String plainText,boolean judgeMD) {   
        StringBuffer buf = new StringBuffer("");   
        try {   
        MessageDigest md = MessageDigest.getInstance("MD5");   
        try {
			md.update(plainText.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("MD5 ERROR:",e);
		}   
        byte b[] = md.digest();   
        int i;   
        for (int offset = 0; offset < b.length; offset++) {   
            i = b[offset];   
            if(i<0) i+= 256;   
            if(i<16)   
            buf.append("0");   
            buf.append(Integer.toHexString(i));   
        }   
  
        } catch (NoSuchAlgorithmException e) {   
        // TODO Auto-generated catch block   
        	log.error("MD5 EROOR",e);
        }   
        if(judgeMD == true){  
            return buf.toString();  
        }else{  
            return buf.toString().substring(8,24);  
        }  
          
    } 
    
    public static void main(String[] args)
    {
    	 String sign = MD5("王植鹏");
    	 System.out.println(sign);
    	 
    	 String signUTF8 = MD5.encryptionUtf8("王植鹏");
       	 System.out.println(signUTF8);
       	 
       	String signGbk = MD5.encryptionGbk("李光明");
      	 System.out.println(signGbk.toUpperCase());
      	System.out.println("7EE283A5381A4E86B74B8CBC0F855777");
    }
}