package org.learn.open.rest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.commons.codec.digest.DigestUtils;

/****
 * 密码加密工具类
 * @author baolongfei
 * @Time 2015年10月26日下午4:48:59
 */
public class MD5DigestUtil {

	public static String getMd5Code(InputStream in) throws IOException {
		return DigestUtils.md5Hex(in);
	}

	public static String getMd5Code(String content) {
		return DigestUtils.md5Hex(content);
	}
	
	private static String hexString = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
	
	/* 
	* 将16进制数字解码成字符串,适用于所有字符（包括中文） 
	*/  
	public static String decode16(String bytes) {  
		
	   ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);  
	   // 将每2位16进制整数组装成一个字节  
	   for (int i = 0; i < bytes.length(); i += 2)  
	    baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));  
	   return new String(baos.toByteArray());  
	} 
	
	/* 
	* 将字符串编码成16进制数字,适用于所有字符（包括中文） 
	*/  
	public static String encode16(String str) {  
	   // 根据默认编码获取字节数组  
	   byte[] bytes = str.getBytes();  
	   StringBuilder sb = new StringBuilder(bytes.length * 2);  
	   // 将字节数组中每个字节拆解成2位16进制整数  
	   for (int i = 0; i < bytes.length; i++) {  
	    sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));  
	    sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));  
	   }  
	   return sb.toString();  
	}  

	public static void main(String[] args) {
		Calendar dueTime = Calendar.getInstance();
		dueTime.add(Calendar.MINUTE, 5);
		System.err.println(dueTime.getTimeInMillis());
		System.out.println(dueTime.getTime());
		String sign = "admin@qq.com_71fae1c33bd862dd22115839c21eeba5_"+dueTime.getTimeInMillis();
		System.err.println(sign);
		System.err.println(encode16(sign));
		
	String enaaa = encode16("按时打算");
	System.out.println("encode16  ->>"+enaaa);
	String deaaa = decode16(enaaa);
	System.out.println("decode16 ->>"+deaaa);
	
	
	}
}
