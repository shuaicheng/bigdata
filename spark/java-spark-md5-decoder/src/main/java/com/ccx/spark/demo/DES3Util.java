package com.ccx.spark.demo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DES3Util {
	private static final String UTF8 = "utf-8";
	private static final String ALGORITHM_DESEDE = "DESede";
	
	private static byte[] build3DesKey(String keyStr) throws Exception {
		byte[] key = new byte[24];
		byte[] temp = keyStr.getBytes(UTF8);
		if (key.length > temp.length) {
			System.arraycopy(temp, 0, key, 0, temp.length);
		} else {
			System.arraycopy(temp, 0, key, 0, key.length);
		}

		return key;
	}

	private static String byte2HexStr(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1) {
				sb.append("0");
			}

			sb.append(s.toUpperCase());
		}

		return sb.toString();
	}

	private static byte[] str2ByteArray(String s) {
		int byteArrayLength = s.length() / 2;
		byte[] b = new byte[byteArrayLength];
		for (int i = 0; i < byteArrayLength; i++) {
			byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16)
					.intValue();
			b[i] = b0;
		}

		return b;
	}

	/**
	 * 鍔犲瘑
	 */
	public static String desedeEncoder(String src, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(build3DesKey(key),
				ALGORITHM_DESEDE);
		Cipher cipher = Cipher.getInstance(ALGORITHM_DESEDE);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] b = cipher.doFinal(src.getBytes(UTF8));

		return byte2HexStr(b);
	}

	/**
	 * 瑙ｅ瘑
	 */
	public static String desedeDecoder(String dest, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(build3DesKey(key),
				ALGORITHM_DESEDE);
		Cipher cipher = Cipher.getInstance(ALGORITHM_DESEDE);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] b = cipher.doFinal(str2ByteArray(dest));

		return new String(b, UTF8);
	}
	
	public static void main(String[] str){
		String key = "88888888";
		String name = "丁家喜";
		String mobile = "15110158682";
		String card = "ID100001";
		String cid = "110108196708179712";
		
		try {
			System.out.println(desedeEncoder(name,key));
			System.out.println(desedeEncoder(mobile,key));
			System.out.println(desedeEncoder(card,key));
			System.out.println(desedeEncoder(cid,key));
			
			System.out.println(desedeDecoder("DA2A9145BB2D6BBE0E86FA907B1B8DF7",key));
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
	}
	
	
}
