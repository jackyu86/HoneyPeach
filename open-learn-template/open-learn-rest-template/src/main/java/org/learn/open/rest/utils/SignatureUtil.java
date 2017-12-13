package org.learn.open.rest.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;


/**
 * 签名算法
 * @author songzhili
 *2015年11月30日上午9:46:48
 */
public class SignatureUtil {
     
	static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) throws ParseException, UnsupportedEncodingException {
		
		Map<String, String> params = new TreeMap<String, String>();
		try {
			params.put("timestamp", URLEncoder.encode("2015-11-30 12:53:00", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(checkTimeStamp(params.get("timestamp")));
	}
	/**
	 * 校验时间戳是否合法
	 * @param sourceTime
	 * @return
	 */
	public static boolean checkTimeStamp(String sourceTime){
		try {
			sourceTime = URLDecoder.decode(sourceTime, "UTF-8");
			long source = format.parse(sourceTime).getTime();
			long cunrrent = System.currentTimeMillis();
			if(source > cunrrent){
				return false;
			}
			return (cunrrent - source) <= 300000?true:false;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * @param params
	 * @return
	 */
	public static String sign(Map<String, String> params){
		/****/
		StringBuilder stringtoSign = new StringBuilder();
		for(Map.Entry<String, String> entry : params.entrySet()){
			if(entry.getKey() != null && entry.getValue() != null
					&& !"".equals(entry.getKey()) && !"".equals(entry.getValue())){
				try {
					stringtoSign.append(entry.getKey()).append(URLDecoder.decode(entry.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		/**得到md5之后的加密串参数转为大写**/
		return signMD5(stringtoSign.toString()).toUpperCase();
	}
	
	/****
	 * 使用MD5加密参数
	 * @param source
	 * @return
	 */
	
	public static String signMD5(String source){
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = source.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
}
