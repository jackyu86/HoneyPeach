package org.learn.open.rest.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import org.learn.open.rest.exception.BaseException;


/**  
 * @ClassName: NetUtils  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author jack-yu
 * @date 2015年9月9日 下午12:02:35  
 *    
 */
public class RestNetUtils {
	public static String buildError(ErrorCodeEnum ErrorCodeEnum){
		StringBuilder sb = new StringBuilder();
		sb/*.append("{ \"errrorResponse\" : ")*/
		.append("{").append("\"errorCode").append("\"").append(":").append("\"")
		.append(ErrorCodeEnum.getValue()).append("\"").append(",").append("\"enErrorMessage")
		.append("\"").append(":").append("\"").append(ErrorCodeEnum.getEnMes()).append("\"").append(",").append("\"zhErrorMessage")
		.append("\"").append(":").append("\"").append(ErrorCodeEnum.getZhMes()).append("\"").append("}")/*.append("}")*/;
		return sb.toString();
	}
	
	public static String  getErrorMesByEx(Throwable throwable){
		ErrorCodeEnum ErrorCodeEnum=null;
		String entity="";
		if (throwable instanceof BaseException) {
 			BaseException baseException = (BaseException) throwable;
 			 ErrorCodeEnum = baseException.getErrorCodeEnum();
 			 entity = RestNetUtils.buildError(ErrorCodeEnum);
 			
 		} else if (throwable instanceof NotFoundException) {
 			ErrorCodeEnum = ErrorCodeEnum.REQUESTNOTFOUND;
 			 entity = RestNetUtils.buildError(ErrorCodeEnum);
 		} else{
 			ErrorCodeEnum = ErrorCodeEnum.SERVICEEXCEPTION.setEnMes(throwable.getMessage());
 			 entity = buildError(ErrorCodeEnum);
 		}
		return entity;
	}
	/**
	 * 获取当前网络ip
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request){
		String ipAddress = request.getHeader("x-forwarded-for");
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
					//根据网卡取本机配置的IP
					InetAddress inet=null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress= inet.getHostAddress();
				}
			}
			//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
				if(ipAddress.indexOf(",")>0){
					ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
				}
			}
			return ipAddress; 
	}

}
