package org.learn.open.rest.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.learn.open.rest.exception.OpenApiException;
import org.learn.open.rest.utils.ErrorCodeEnum;
import org.learn.open.rest.utils.RateLimterUtils;
import org.learn.open.rest.utils.RestNetUtils;
import org.learn.open.rest.utils.SignatureUtil;


/**   
 * 
 * @author jack  
 * @version   
 *       1.0, 2015年5月8日 下午6:52:02   
 */
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class UserAuthContainerRequestFilter implements ContainerRequestFilter{
    private static final Logger LOGGER = Logger.getLogger(UserAuthContainerRequestFilter.class);
	//private static final String  REQUESTLIMITCACHENAME = "request-limit-";
	//private static final String OAUTHCODEKEY = "oauthcode-";
	private static final String OAUTHTOKENKEY = "oauthtoken-";
	
	@Context
	private HttpServletRequest request;
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		final Charset CHARACTER_SET = Charset.forName("utf-8");  
		String authHeader = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);  
		Enumeration<String>  enumeration =	request.getParameterNames();
		Map<String, String> paramsMap = new TreeMap<String, String>();
		String signature = "";
		for(Enumeration e=enumeration;e.hasMoreElements();){
		       String thisName=e.nextElement().toString();
		       String thisValue=request.getParameter(thisName);
		       if(!"sign".equals(thisName)){
		    	   paramsMap.put(thisName, thisValue);
		       }
		       if("sign".equals(thisName)){
		    	   signature = thisValue;
		       }
		}
		/*校验时间戳*/
		if(paramsMap.containsKey("timeStamp")){
			if(!SignatureUtil.checkTimeStamp(paramsMap.get("timeStamp"))){
				requestContext.setProperty("cloudShopErrorCode",
						ErrorCodeEnum.TIMESTAMPINVALID);
				throw new OpenApiException(null,ErrorCodeEnum.TIMESTAMPINVALID.setZhMes("请求时间超过十分钟"));
			}
		}/*else{
			throw new OpenApiException(null,ErrorCodeEnum.PARAMETERINVALIDRROR.setZhMes("缺失时间搓 ：timeStamp"));
		}*/
		/*校验签名*/
		if(signature != null && !"".equals(signature) ){
			String sign = SignatureUtil.sign(paramsMap);
			if(!signature.equals(sign)){
				requestContext.setProperty("cloudShopErrorCode",
						ErrorCodeEnum.SIGNATUREERROR);
				throw new OpenApiException(null,ErrorCodeEnum.SIGNATUREERROR.setZhMes("签名错误,请检查请求参数"));
			}
		}/*else{
			throw new OpenApiException(null,ErrorCodeEnum.PARAMETERINVALIDRROR.setZhMes("缺失签名 ：sign"));
		}*/
		

        if (authHeader != null && authHeader.startsWith("Basic")) {
            String decoded = new String(Base64.getDecoder().decode(authHeader.substring(6).getBytes()), CHARACTER_SET);  

      	  final String[] split;
      	  final String appKey;
      	  final String appSecret;
      	  final String accessToken;

            try {
            	 split = decoded.split(":");
            	 appKey = split[0];
            	 appSecret = split[1];
            	 accessToken = split[2];
			} catch (Exception e) {
				requestContext.setProperty("cloudShopErrorCode",
						ErrorCodeEnum.AUTHPARAMERROR);
				throw new OpenApiException(null,ErrorCodeEnum.AUTHPARAMERROR.setZhMes("授权参数格式错误"));
			}
           
				//check appKey
				if(appKey==null||"".equals(appKey.trim())){
					throw new OpenApiException(null,ErrorCodeEnum.AUTHPARAMERROR.setZhMes("授权参数错误缺失appKey"));
				}
				//check appSecret
				if(appSecret==null||"".equals(appSecret.trim())){
					throw new OpenApiException(null,ErrorCodeEnum.AUTHPARAMERROR.setZhMes("授权参数错误缺失appSecret"));
				}
				//check accessToken
				if(accessToken==null||"".equals(accessToken.trim())){
					throw new OpenApiException(null,ErrorCodeEnum.AUTHPARAMERROR.setZhMes("授权参数错误缺失accessToken"));
				}
				//this.stringRedisTemplate.opsForValue().get(OAUTHTOKENKEY + accessToken);
				String tenantId ="A01122";	/*RedisClient.getJedis().get(OAUTHTOKENKEY + accessToken);*/
				/**
				 * @TODO 验证绑定关系
				 * 
				 */
			
				//check appKey & accessToken
				if (tenantId == null&&"".equals(tenantId.trim())) {
					throw  new OpenApiException(null, ErrorCodeEnum.ACCESSTOKENERROR.setZhMes("授权不存在或已过期"));
				}
				//check relations
				if(!appKey.intern().equals(tenantId.trim().intern())){
					throw new OpenApiException(null,ErrorCodeEnum.ACCESSTOKENTENANTIDERROR);
				}
				// get callback url
				/**
				 * 
				 * @TODO 获取应用回调
				 * 
				 */
				//操作租户id
				request.setAttribute("tenantId", tenantId);
				request.setAttribute("merchantId",accessToken);
				//操作用户名
				request.setAttribute("opName", tenantId);
				request.setAttribute("cusIp",RestNetUtils.getIpAddr(request)!=null? RestNetUtils.getIpAddr(request):"");
				//令牌桶
				try {
					RateLimterUtils.enter(appKey);
				} catch (RateLimterUtils.RateLimitException e) {
				      throw new  OpenApiException(null,ErrorCodeEnum.REQTOOFREQUENTLY.setTenantId(appKey));
				}
            	
				requestContext.setSecurityContext(new SecurityContext() {  
                    @Override  
                    public Principal getUserPrincipal() {  
                        return new Principal() {  
                            @Override  
                            public String getName() {  
                                return tenantId;  
                            }  
                        };  
                    }  

                    @Override  
                    public boolean isUserInRole(String role) {  
                        return true;  
                    }  

                    @Override  
                    public boolean isSecure() {  
                        return false;  
                    }  

                    @Override  
                    public String getAuthenticationScheme() {  
                        return "BASIC";  
                    }  
                });  
                return;  

           
        } else{
        	requestContext.setProperty("cloudShopErrorCode", ErrorCodeEnum.AUTHISNULL);
        	throw new BadRequestException("The authorization accounting information is empty");
      }
        
      
        
	}
	


}
