package org.learn.open.rest.async.thread;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.learn.open.rest.exception.BaseException;
import org.learn.open.rest.exception.OpenApiException;
import org.learn.open.rest.utils.ErrorCodeEnum;
import org.learn.open.rest.utils.RestNetUtils;


/**  
 * @ClassName: BaseFunctionAsync  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author jack-yu
 * @date 2016年3月15日 下午12:37:19  
 *    
 */
public abstract class BaseAsyncThread {
	private static final Logger LOGGER = Logger.getLogger(BaseAsyncThread.class);
    private  static final long TIMEOUT = 20;    
	
    public void registers(AsyncResponse asyncResponse){
    	asyncResponse.setTimeout(TIMEOUT, TimeUnit.SECONDS);
    	
    	//超时处理
		   asyncResponse.setTimeoutHandler(new TimeoutHandler() {
		        @Override
		        public void handleTimeout(AsyncResponse asyncResponse) {
		            asyncResponse.resume(Response.status(Response.Status.OK)
		                    .entity(RestNetUtils.buildError(ErrorCodeEnum.SERVICETIMEOUTERROR)).build());
		        }
		    });
         
    	 asyncResponse.register(new CompletionCallback() {
             @Override
             public void onComplete(Throwable throwable) {
                 if (throwable == null) {
                     LOGGER.info("CompletionCallback-onComplete: OK");
                 } else {
             		ErrorCodeEnum ErrorCodeEnum=null;
             		String entity="";
             		if (throwable instanceof BaseException) {
             			BaseException baseException = (BaseException) throwable;
             			 ErrorCodeEnum = baseException.getErrorCodeEnum();
             			 entity = RestNetUtils.buildError(ErrorCodeEnum);
             			
             		} else if (throwable instanceof NotFoundException) {
             			ErrorCodeEnum = ErrorCodeEnum.REQUESTNOTFOUND;
             			 entity = RestNetUtils.buildError(ErrorCodeEnum);
             		}else{
             			ErrorCodeEnum = ErrorCodeEnum.SERVICEEXCEPTION.setEnMes(throwable.getMessage());
             			 entity = RestNetUtils.buildError(ErrorCodeEnum);
             		}
             		LOGGER.error(new StringBuilder().append("errorCode:").append(ErrorCodeEnum.getValue()).append(",en:").append(ErrorCodeEnum.getEnMes()).append(",zh:").append(ErrorCodeEnum.getZhMes()).append(",tenantId:").append(ErrorCodeEnum.getTenantId()).toString());
             		throw  new OpenApiException(null, ErrorCodeEnum);
             		/* Response.ok(entity, MediaType.APPLICATION_JSON).status(Status.OK)
             				.build();*/
                 }
             }
         });

         asyncResponse.register(new ConnectionCallback() {
             @Override
             public void onDisconnect(AsyncResponse disconnected) {
                 //Status.GONE=410
                 LOGGER.info("ConnectionCallback-onDisconnect");
                 disconnected.resume(Response.status(Response.Status.GONE).entity("disconnect!").build());
             }
         });
    }
	
}
