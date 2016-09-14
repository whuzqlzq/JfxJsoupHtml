package zq.javafx.jsoup.html.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

	public static String md5(String str) {
		return md5(str, "utf-8", 32);
	}

	// MD5加密算法,str=要加密的字符串,charset=字符编码,bit=位数16和32
	public static String md5(String str, String charset, int bit) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes(charset));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		if (bit == 32) {
			return md5StrBuff.toString();
		} else {
			return md5StrBuff.toString().substring(8, 24);
		}
	}

	// URL编码,str=欲转化的字符串,charest=字符编码
	public static String urlEncoder(String str, String charest) {
		String result = "";
		try {
			result = URLEncoder.encode(str, charest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	// URL解码
	public static String urlDecoder(String str, String charest) {
		String result = "";
		try {
			result = URLDecoder.decode(str, charest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	// SHA1加密算法
	public static String SHA1(String str) {
		MessageDigest md = null;
		String strSHA1 = null;
		byte[] strBytes = str.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(strBytes);
			strSHA1 = bytesToHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return strSHA1;
	}

	private static String bytesToHex(byte[] bytes) {
		String hex = "";
		String tmp = null;
		for (int i = 0; i < bytes.length; i++) {
			tmp = (Integer.toHexString(bytes[i] & 0xFF));
			if (tmp.length() == 1) {
				hex += "0";
			}
			hex += tmp;
		}
		return hex;
	}

}